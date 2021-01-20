package drugBankTD

import java.io.{FileWriter, IOException}
import java.util
import java.lang
import java.util.Date
import com.github.javafaker.Faker
import org.apache.jena.rdf.model.ModelFactory

import java.text.SimpleDateFormat
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

class LubmExtractor(val dbSource: String, val male: Int, val vaccinationPercent: Int, vaccinesRepartition: util.ArrayList[Int], vaccines: util.ArrayList[String], val subjects :  util.ArrayList[String]) {

  vaccinesRepartition.forEach(e => if(e < 0 || e > 100) throw new IllegalArgumentException("Each element of vaccinesRepartition should be between 0 and 100"))
  vaccines.forEach(e => if(e.isEmpty) throw new IllegalArgumentException("Each element of vaccine should be non empty"))
  if (vaccinesRepartition.size != vaccines.size) throw new IllegalArgumentException("VaccinesRepartition size must be equals than vaccines")
  if (vaccinesRepartition.reduce(_+_) != 100) throw new IllegalArgumentException("VaccinesRepartition sum must be equals than 100")
  if (male > 100 || male < 0) throw new IllegalArgumentException("Mal percentage should be between 0 and 100")
  if (vaccinationPercent > 100 || vaccinationPercent < 0) throw new IllegalArgumentException("vaccined percentage should be between 0 and 100")

  // nb: sujet, predicat, objet
  //     ressource, property, ressource / litteral

  // Q3.1
  val model = ModelFactory.createDefaultModel()

  private val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
  private val rdfType = model.createProperty(typeProperty)
  private val sdf = new SimpleDateFormat("dd/MM/yyyy")
  private val twenty : Date = sdf.parse("01/01/2001")
  private val thirty : Date = sdf.parse("01/01/1991")
  private val seventy : Date = sdf.parse("01/01/1951")
  private val all_subjects = List[String]()



  def load() = {
    model.read(dbSource, "TTL")
  }

  def monSuperTest() = {
    val iterator = model.listSubjects()
    val res = scala.collection.mutable.Set[String]()
    while (iterator.hasNext) {
      res.add(iterator.next().getURI)
    }
    printf("result: " + res.toString())
  }

  def monGigaTest() = {
    println("type property size " + model.listSubjectsWithProperty(rdfType).toList().size())
    subjects.forEach(subject => {
      val obj = model.createResource(subject)
      val iterator = model.listSubjectsWithProperty(rdfType, obj)
      var i = 0
      while (iterator.hasNext()) {
        iterator.next()
        //printf(iterator.next().getURI.toString + "\n")
        i = i + 1
      }
      println(subject + ": " + i)
    })
  }

  /**
   * Display all people and the number of people
   */
  def monPetitTest() = {
    println("type property size " + model.listSubjectsWithProperty(rdfType).toList().size())
    val iterator = model.listSubjectsWithProperty(rdfType)
    var i = 0
    while (iterator.hasNext()) {
      val uri = iterator.next().getURI.toString
      val profession = uri.split("/")

      if (profession.length == 4 && (uri.contains("AssistantProfessor") || uri.contains("AssociateProfessor") || uri.contains("FullProfessor") || uri.contains("GraduateStudent") || uri.contains("Lecturer") || uri.contains("UndergraduateStudent"))) {
        i = i + 1
        //println(uri)
      }
    }
    println("Tous les gens: " + i)
  }


  /**
   * created random data based on subjectType, and class parameters
   * @param subjectType URI represents a person owning a property on his professional situation
   */
  def extender() = {
    subjects.forEach(subjectType => {
      val iterator = model.listSubjectsWithProperty(rdfType, model.createResource(subjectType))
      var low_birth_range = thirty
      var hight_birth_range = seventy
      if(! isOlder30(subjectType)){
        low_birth_range = twenty
        hight_birth_range = thirty
      }

      while (iterator.hasNext()) {
        val faker = new Faker()
        var uri = iterator.next().getURI
        model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#fName"), model.createLiteral(faker.name.firstName()))    // firstName
        model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#lName"), model.createLiteral(faker.name.lastName()))   //lastName
        model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#gender"), model.createResource("http://extension.group1.fr/onto#" + randomGender()))   //Genre
        model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#zipcode"), model.createLiteral(faker.address.zipCode()))   //ZipCode
        model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#birhtdate"), model.createLiteral(faker.date().between(hight_birth_range, low_birth_range).toString))
        model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#state"), model.createLiteral(faker.address.state())) // Address
        model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#id"), model.createLiteral(faker.idNumber().toString)) // ID
      }
    })

  }

  /**
   * adds properties of vaccines to some person based on vaccinationPercent
   * @param subjectType URI represents a person owning a property on his professional situation
   */
  def extender_vaccine() = {
    subjects.forEach(subjectType => {
      val iterator = model.listSubjectsWithProperty(rdfType, model.createResource(subjectType))
      while (iterator.hasNext()) {
        val faker = new Faker()
        var uri = iterator.next().getURI
        if (randomVaccinator()) model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#vaccine"), model.createResource("http://extension.group1.fr/onto#" + randomVaccine()))
      }
    })
  }

  /**
   * TODO changer ça et faire en sorte qu'on conaisse les ages grçace au fichier de config
   * detects the age range of the person based on URI
   * @param subjectType URI represents a person owning a property on his professional situation
   * @return
   */
  private def isOlder30(subjectType: String) : Boolean = {
    val position = subjectType.indexOf("#")
    val profession = subjectType.substring(position)
    if (profession.equals("#AssistantProfessor") || profession.equals("#AssociateProfessor") || profession.equals("#FullProfessor") || profession.equals("#Lecturer")) {
      return true
    }
    return false
  }

  /**
   * shooting at random according to the number of male percentage given in parameter the class
   * @return Male or Female
   */
  private def randomGender() : String = {
    if (new Faker().number.numberBetween(0, 100) < male)  "Male"
    else "Female"
  }

  /**
   * shooting at random according to the number of people percentage given in parameter the class
   * @return true if the person is vaccinated, false otherwise
   */
  private def randomVaccinator() : Boolean = {
    new Faker().number.numberBetween(0, 100) < vaccinationPercent
  }

  /**
   * give a vaccine at random according to the probabilities given in parameter the class
   * @return name of vaccine
   */
  private def randomVaccine() : String = {
    var rand = new Faker().number.numberBetween(0, 100)
    var i: Int = 0
    while (i < vaccinesRepartition.size()){
      rand = rand - vaccinesRepartition.get(i)
      if (rand < 0) vaccines.get(i)
      i += 1
    }
    vaccines.get(0)
  }

  /**
   * encode the model in rdf format
   * @param fileName filename where the model will be encoded
   */
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