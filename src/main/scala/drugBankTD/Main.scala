package drugBankTD

import drugBankTD.LubmExtractor

object Main extends App {
  val db = new LubmExtractor("file:C:\\Users\\pjgal\\OneDrive\\Bureau\\lubm1.ttl")
  db.load()
  //db.monSuperTest()
  db.monPetitTest()
}

