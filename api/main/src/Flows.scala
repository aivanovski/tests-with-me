object Flows {

  val ResetData =
    """
      |- name: Reset data
      |- project: KeePassVault
      |
      |- sendBroadcast:
      |    name: com.ivanovsky.passnotes.automation/com.ivanovsky.passnotes.domain.test.TestDataBroadcastReceiver
      |    data:
      |      - key: isResetAppData
      |        value: true
      |
      |- launch: com.ivanovsky.passnotes.automation
      |
      |- assertVisible: No databases
      |""".stripMargin

  val SetupBasicKdbxDatabase =
    """
      |- name: Setup basic.kdbx database
      |- project: KeePassVault
      |
      |- sendBroadcast:
      |    name: com.ivanovsky.passnotes.automation/com.ivanovsky.passnotes.domain.test.TestDataBroadcastReceiver
      |    data:
      |      - key: fakeFileName
      |        value: basic.kdbx
      |
      |
      |- launch: com.ivanovsky.passnotes.automation
      |
      |- assertVisible: basic.kdbx
      |""".stripMargin

  val UnlockDatabaseWithPassword =
    """
      |- name: Unlock database with password
      |- project: KeePassVault
      |
      |- runFlow: Reset data
      |- runFlow: Setup basic.kdbx database
      |
      |- launch: com.ivanovsky.passnotes.automation
      |
      |- assertVisible: basic.kdbx
      |
      |
      |- inputText:
      |    input: abc123
      |    contentDescription: Password
      |
      |- tapOn:
      |    contentDescription: Unlock button
      |
      |- waitUntil:
      |    notVisible:
      |      contentDescription: Loading indicator
      |    step: 1
      |    timeout: 10
      |
      |- tapOn:
      |    text: DISABLE
      |    when:
      |      visible:
      |        hasText: Would you like to enable system notification for opened database
      |
      |- assertVisible: Basic entry
      |""".stripMargin

}
