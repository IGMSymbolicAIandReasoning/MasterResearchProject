package drugBankTD

import java.text.SimpleDateFormat
import java.util
import java.util.Date
import util.ArrayList
import util.Arrays

object LabelBase {

  val INPUT_FILE : String = "file:///home/hoomar/Documents/m2/IA/ressources/lubm1.ttl"
  val OUTPUT_FILE : String = "myRdf.rdf"

  val VACCINES_NAMES = new ArrayList[String](Arrays.asList("Pfizer", "Moderna","AstraZeneca", "SpoutnikV", "CanSinoBi"))
  val VACINES_PERCENT = new ArrayList[Int](Arrays.asList(20, 20, 30, 10, 20))

  val MALE_PERCENT : Int = 40
  val PEOPLE_VACCINATED_PERCENT : Int = 70
}
