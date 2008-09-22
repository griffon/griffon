application(title:'AbeilleFormsDemo', size:[300,140]) {
   formPanel( id: "form", form: "login.jfrm" )
}

actions {
   /*
   action( id: "loginAction",
      closure: { evt ->
          println evt
      }
   )
   */
   // TODO find a way to set loginAction on loginBtn 
   // without writing over previous properties
   bean( form.loginBtn, actionPerformed: controller.showInfo )
   bean( model, username: bind { form.username.text } )
}
