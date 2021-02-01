package drugBankTD

import java.io.{FileWriter, IOException}
import java.util
import java.util.ArrayList
import java.lang
import java.util.Date

import com.github.javafaker.Faker
import org.apache.jena.rdf.model.{Model, ModelFactory}
import java.text.SimpleDateFormat

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.util.control.Breaks.break

class LubmExtractor(val dbSource: String, val male: Int, val vaccinationPercent: Int, vaccinesRepartition: util.ArrayList[Int], vaccines: util.ArrayList[String], val subjects:  util.ArrayList[String]) {

  vaccinesRepartition.forEach(e => if(e < 0 || e > 100) throw new IllegalArgumentException("Each element of vaccinesRepartition should be between 0 and 100"))
  vaccines.forEach(e => if(e.isEmpty) throw new IllegalArgumentException("Each element of vaccine should be non empty"))
  if (vaccinesRepartition.size != vaccines.size) throw new IllegalArgumentException("VaccinesRepartition size must be equals than vaccines")
  if (vaccinesRepartition.sum != 100) throw new IllegalArgumentException("VaccinesRepartition sum must be equals than 100")
  if (male > 100 || male < 0) throw new IllegalArgumentException("Mal percentage should be between 0 and 100")
  if (vaccinationPercent > 100 || vaccinationPercent < 0) throw new IllegalArgumentException("vaccined percentage should be between 0 and 100")

  // nb: sujet, predicat, objet
  //     ressource, property, ressource / litteral

  // Q3.1
  val model: Model = ModelFactory.createDefaultModel()

  private val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
  private val rdfType = model.createProperty(typeProperty)
  private val sdf = new SimpleDateFormat("dd/MM/yyyy")
  private val twenty : Date = sdf.parse("01/01/2001")
  private val thirty : Date = sdf.parse("01/01/1991")
  private val seventy : Date = sdf.parse("01/01/1951")
  private val all_subjects = new util.ArrayList[String]()



  /**
   * Load data in model
   */
  def load(): Unit = {
    model.read(dbSource, "TTL")
    load_resources()
  }


  /**
   * create a list with all the subjects having the desired properties
   */
  def load_resources(): Unit = {
    val subject_id : util.ArrayList[String] = subjects.foldLeft(new util.ArrayList[String]())((acc, s) => {
      acc.add(s.substring(s.indexOf("#") + 1))
      acc
    })
    val iterator = model.listSubjectsWithProperty(rdfType)
    while (iterator.hasNext) {
      val uri_done = iterator.next().getURI
      val uri = uri_done
      val suject = uri.split("/")
      if (suject.length == 4){
        subject_id.forEach(x => {
          if (uri.contains(x)) all_subjects.add(uri_done)
        })
      }
    }
    println("They are " + all_subjects.size + " subjects with the desired properties. ")
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
    val rand = new Faker().number.numberBetween(0, 100)
    var i: Int = 0
    var acc = 0
    while (i < vaccinesRepartition.size()){
      acc = acc + vaccinesRepartition.get(i)
      if (acc >= rand) {
        return vaccines.get(i)
      }
      i = i + 1
    }
    vaccines.get(i)
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
      case _: IOException =>
      // ignore
    }
  }

  /**
   * detects the age range of the person based on URI
   * @param subjectType URI represents a person owning a property on his professional situation
   * @return
   */
  private def isOlder30(subjectType: String) : Boolean = {
    if (subjectType.contains("AssistantProfessor") || subjectType.contains("AssociateProfessor") || subjectType.contains("FullProfessor") || subjectType.contains("Lecturer")) {
      return true
    }
    false
  }

  /**
   * created random data based on subjectType, and class parameters
   */
  def extender(): Unit = {
    val faker = new Faker()
    all_subjects.forEach(uri => {
      var low_birth_range = thirty
      var hight_birth_range = seventy
      if(! isOlder30(uri)) {
        low_birth_range = twenty
        hight_birth_range = thirty
      }
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#fName"), model.createLiteral(faker.name.firstName()))    // firstName
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#lName"), model.createLiteral(faker.name.lastName()))   //lastName
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#gender"), model.createResource("http://extension.group1.fr/onto#" + randomGender()))   //Genre
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#zipcode"), model.createLiteral(faker.address.zipCode()))   //ZipCode
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#birhtdate"), model.createLiteral(faker.date().between(hight_birth_range, low_birth_range).toString))
      model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#id"), model.createLiteral(faker.idNumber().toString)) // ID
    })
  }

  /**
   * adds properties of vaccines to some person based on vaccinationPercent
   */
  def extender_vaccine(): Unit = {
    val faker = new Faker()
    all_subjects.forEach(uri => {
      if (randomVaccinator()) model.add(model.createResource(uri), model.createProperty("http://extension.group1.fr/onto#vaccine"), model.createResource("http://extension.group1.fr/onto#" + randomVaccine()))
    })
  }
}