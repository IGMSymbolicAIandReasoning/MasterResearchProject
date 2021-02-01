package drugBankTD

import java.text.SimpleDateFormat
import java.util
import java.util.Date
import util.ArrayList
import util.Arrays

object LabelBase {

  val INPUT_FILE : String = "file:///C:\\Users\\pjgal\\OneDrive\\Bureau\\UPEM_MATH_INFO\\M2\\IA\\lubm1.ttl"
  val OUTPUT_FILE : String = "lubm1_extended.rdf"

  val SUJECTS = new util.ArrayList[String](util.Arrays.asList("http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssistantProfessor", "http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssociateProfessor","http://swat.cse.lehigh.edu/onto/univ-bench.owl#FullProfessor", "http://swat.cse.lehigh.edu/onto/univ-bench.owl#GraduateStudent","http://swat.cse.lehigh.edu/onto/univ-bench.owl#Lecturer", "http://swat.cse.lehigh.edu/onto/univ-bench.owl#UndergraduateStudent"))
  val VACCINES_NAMES = new util.ArrayList[String](util.Arrays.asList("Pfizer", "Moderna","AstraZeneca", "SpoutnikV", "CanSinoBi"))
  val VACINES_PERCENT = new util.ArrayList[Int](util.Arrays.asList(20, 20, 30, 10, 20))

  val MALE_PERCENT : Int = 40
  val PEOPLE_VACCINATED_PERCENT : Int = 70
}
