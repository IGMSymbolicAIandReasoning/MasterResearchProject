package drugBankTD

import java.text.SimpleDateFormat
import java.util


object Main extends App {




  val db = new LubmExtractor(LabelBase.INPUT_FILE, LabelBase.MALE_PERCENT, LabelBase.PEOPLE_VACCINATED_PERCENT, LabelBase.VACINES_PERCENT, LabelBase.VACCINES_NAMES)


  db.load()

  db.extender("http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssistantProfessor")

  // studen been between 20 and 30 years old
  // professor’s age is between 30 and 70

  db.toFile(LabelBase.OUTPUT_FILE)

}

