package drugBankTD

import java.text.SimpleDateFormat
import java.util


object Main extends App {
  var collect = util.Arrays.asList(20, 20, 30, 10, 20)
  val vaccinesRepartition = new util.ArrayList[Int](collect)

  var collect2 = util.Arrays.asList("Pfizer", "Moderna","AstraZeneca", "SpoutnikV", "CanSinoBi")
  val vaccines = new util.ArrayList[String](collect2)
  val db = new LubmExtractor(LabelBase.INPUT, 70, 70, vaccinesRepartition, vaccines)
  db.load()
  val sdf = new SimpleDateFormat("dd/MM/yyyy")
  db.extender("http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssistantProfessor", sdf.parse("01/01/1951"), sdf.parse("01/01/1991"))
  // Young = ("01/01/1991") ("01/01/2001")
  // Old = ("01/01/1951") ("01/01/1991")

  db.toFile("bitemalade")

}

