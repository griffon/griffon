application {
	title="Greet"
	startupGroups=["Greet"]
	autoShutdown=true
}
mvcGroups {
	LoginPane {
		model="greet.LoginPaneModel"
		actions="greet.LoginPaneActions"
		view="greet.LoginPaneView"
		controller="greet.LoginPaneController"
	}
	UserPane {
		model="greet.UserPaneModel"
		controller="greet.UserPaneController"
		view="greet.UserPaneView"
	}
	TimelinePane {
		model="greet.TimelinePaneModel"
		view="greet.TimelinePaneView"
		controller="greet.TimelinePaneController"
	}
	Greet {
		model="greet.GreetModel"
		actions="greet.GreetActions"
		controller="greet.GreetController"
		view="greet.GreetView"
	}
}
