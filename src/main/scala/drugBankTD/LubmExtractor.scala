package drugBankTD

import java.util
import java.text.SimpleDateFormat
import java.io.FileOutputStream

import java.io.FileWriter
import java.io.PrintWriter
import com.github.javafaker.Faker
import org.apache.jena.rdf.model.{ModelFactory, Property}
import java.util.Date
import scala.collection.mutable.ListBuffer

class LubmExtractor(val dbSource: String) {

  // nb: sujet, predicat, objet
  //     ressource, property, ressource
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


  def extender(subjectType: String) = {
    val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
    val rdfType = model.createProperty(typeProperty)
    val obj = model.createResource(subjectType)
    val iterator = model.listSubjectsWithProperty(rdfType, obj)

    // Ajouter des bails a chaque type de personne pour etendre le bordel
    while (iterator.hasNext()) {
      printf(iterator.next().getURI.toString + "\n")
      val faker = new Faker()
      val id = faker.idNumber()
      val sdf = new SimpleDateFormat("dd/MM/yyyy")
      val youngAge = faker.date().between(sdf.parse("01/01/1991"), sdf.parse("01/01/2001"))
      //val oldAge = faker.date().between(sdf.parse("01/01/1951"), sdf.parse("01/01/1991"))
      val name = faker.name.fullName() // Miss Samanta Schmidt
      val firstName = faker.name.firstName() // Emory
      val lastName = faker.name.lastName() // Barton
      val gender = generateGender()
      val zipcode = faker.address.zipCode() // 60018 Sawayn Brooks Suite 449
      model.add(model.createResource(iterator.next().getURI), model.createProperty("firstName"), model.createResource(firstName))
      model.add(model.createResource(iterator.next().getURI), model.createProperty("lastName"), model.createResource(lastName))
      model.add(model.createResource(iterator.next().getURI), model.createProperty("gender"), model.createResource(gender))
      model.add(model.createResource(iterator.next().getURI), model.createProperty("zipcode"), model.createResource(zipcode))
      model.add(model.createResource(iterator.next().getURI), model.createProperty("age"), model.createResource(youngAge.toString))
      model.add(model.createResource(iterator.next().getURI), model.createProperty("id"), model.createResource(id.toString))
    }
  }

  def generateGender() : String = {
    if (new Faker().number.numberBetween(0, 1) == 0) {
      "Male"
    }
    else {
      "Female"
    }
  }
}