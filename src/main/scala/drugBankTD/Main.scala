package drugBankTD

import java.text.SimpleDateFormat
import java.util


object Main extends App {

  val db = new LubmExtractor(LabelBase.INPUT_FILE, LabelBase.MALE_PERCENT, LabelBase.PEOPLE_VACCINATED_PERCENT, LabelBase.VACINES_PERCENT, LabelBase.VACCINES_NAMES, LabelBase.SUJECTS)


  db.load()

  db.monGigaTest()

  db.monPetitTest()


  db.extender()
  println("extender : done")
  db.extender_vaccine()
  println("extender vaccine : done")
  db.toFile(LabelBase.OUTPUT_FILE)
  println("write file : done")

}

