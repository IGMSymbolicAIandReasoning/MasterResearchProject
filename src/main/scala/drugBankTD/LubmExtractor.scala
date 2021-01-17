package drugBankTD

import java.io.{FileWriter, IOException}
import java.util
import java.lang

import com.github.javafaker.Faker
import org.apache.jena.rdf.model.ModelFactory

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

class LubmExtractor(val dbSource: String, val male: Int, val vaccinationPercent: Int, vaccinesRepartition: util.ArrayList[Int], vaccines: util.ArrayList[String]) {

  vaccinesRepartition.forEach(e => if(e < 0 || e > 100) throw new IllegalArgumentException("Each element of vaccinesRepartition should be between 0 and 100"))
  vaccines.forEach(e => if(e.isEmpty) throw new IllegalArgumentException("Each element of vaccine should be non empty"))
  if (vaccinesRepartition.size != vaccines.size) throw new IllegalArgumentException("VaccinesRepartition size must be equals than vaccines")
  if (vaccinesRepartition.reduce(_+_) != 100) throw new IllegalArgumentException("VaccinesRepartition sum must be equals than 100")
  if (male > 100 || male < 0) throw new IllegalArgumentException("Mal percentage should be between 0 and 100")
  if (male > 100 || male < 0) throw new IllegalArgumentException("Mal percentage should be between 0 and 100")

  // nb: sujet, predicat, objet
  //     ressource, property, ressource / litteral
  val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
  // Q3.1
  val model = ModelFactory.createDefaultModel()

  def load() = model.read(dbSource, "TTL")

  def monSuperTest() = {
    val iterator = model.listSubjects()
    val res = scala.collection.mutable.Set[String]()
    while (iterator.hasNext) {
      res.add(iterator.next().getURI)
    }
    printf("result: " + res.toString())
  }

  def monGigaTest() = {
    val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
    val rdfType = model.createProperty(typeProperty)
    println("type property size " + model.listSubjectsWithProperty(rdfType).toList().size())
    val obj = model.createResource("http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssistantProfessor")
    val iterator = model.listSubjectsWithProperty(rdfType, obj)
    var i = 0
    while (iterator.hasNext()) {
      printf(iterator.next().getURI.toString + "\n")
      i = i + 1
    }
    print("AssistantProfessor: " + i)
  }

  def monPetitTest() = {
    val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
    val rdfType = model.createProperty(typeProperty)
    println("type property size " + model.listSubjectsWithProperty(rdfType).toList().size())
    val iterator = model.listSubjectsWithProperty(rdfType)
    var i = 0
    while (iterator.hasNext()) {
      var uri = iterator.next().getURI.toString

      if (uri.contains("AssistantProfessor") || uri.contains("AssociateProfessor") || uri.contains("FullProfessor") || uri.contains("GraduateStudent") || uri.contains("Lecturer") || uri.contains("UndergraduateStudent")) {
        i = i + 1
        printf(uri + "\n")
      }
    }
    print("Tous les gens: " + i)
  }


  def extender(subjectType: String, bornBefore: java.util.Date, bornAfter: java.util.Date) = {
    val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
    val rdfType = model.createProperty(typeProperty)
    val obj = model.createResource(subjectType)
    val iterator = model.listSubjectsWithProperty(rdfType, obj)
    while (iterator.hasNext()) {
      val faker = new Faker()
      var uri = iterator.next().getURI
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#fName"), model.createLiteral(faker.name.firstName()))
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#lName"), model.createLiteral(faker.name.lastName()))
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#gender"), model.createResource("http://extension.group1.fr/onto#" + randomGender()))
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#zipcode"), model.createLiteral(faker.address.zipCode()))
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#birhtdate"), model.createLiteral(faker.date().between(bornAfter, bornBefore).toString))
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#state"), model.createLiteral(faker.address.state()))
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#id"), model.createLiteral(faker.idNumber().toString))
      if (randomVaccinator()) model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#vaccine"), model.createResource("http://extension.group1.fr/onto#" + randomVaccine()))
    }
  }

  def randomGender() : String = {
    if (new Faker().number.numberBetween(0, 100) < male)  "Male"
    else "Female"
  }

  def randomVaccinator() : Boolean = {
    new Faker().number.numberBetween(0, 100) < vaccinationPercent
  }

  def randomVaccine() : String = {
    var rand = new Faker().number.numberBetween(0, 100)
    var i: Int = 0
    while (i < vaccinesRepartition.size()){
      rand = rand - vaccinesRepartition.get(i)
      if (rand < 0) vaccines.get(i)
      i += 1
    }
    vaccines.get(0)
  }

  def toFile(fileName: String): Unit = {
    val out = new FileWriter(fileName)
    try model.write(out, "RDF/XML-ABBREV")
    finally try out.close()
    catch {
      case closeException: IOException =>
      // ignore
    }
  }
}