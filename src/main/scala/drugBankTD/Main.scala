package drugBankTD

import java.text.SimpleDateFormat
import java.util


object Main extends App {

  val db = new LubmExtractor(LabelBase.INPUT_FILE, LabelBase.MALE_PERCENT, LabelBase.PEOPLE_VACCINATED_PERCENT, LabelBase.VACINES_PERCENT, LabelBase.VACCINES_NAMES, LabelBase.SUJECTS)

  db.load()

  print("creates data : ")
  db.extender()
  println("done.")

  print("creates vaccine data : ")
  db.extender_vaccine()
  println("done.")

  print("writes in file : ")
  db.toFile(LabelBase.OUTPUT_FILE)
  println("done")
}

