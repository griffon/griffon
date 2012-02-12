package griffon.util
/**
 * @author Graeme Rocher
 * @since 1.1
 * 
 * Created: Dec 12, 2008
 */

public class EnvironmentTests extends GroovyTestCase{

    protected void tearDown() {
        System.setProperty(Environment.KEY, "")

        Metadata.getCurrent().clear()
    }

    void testExecuteForEnvironment() {
        System.setProperty("griffon.env", "prod")

        assertEquals Environment.PRODUCTION, Environment.getCurrent()


        assertEquals "prod", Environment.executeForCurrentEnvironment {
            environments {
                production {
                    "prod"
                }
                development {
                    "dev"
                }
                soe {
                    "some other environment"
                }
            }
        }


        assertEquals "dev", Environment.executeForEnvironment(Environment.DEVELOPMENT) {
            environments {
                production {
                    "prod"
                }
                development {
                    "dev"
                }
                soe {
                    "some other environment"
                }
            }
        }

        System.setProperty("griffon.env", "dev")

        assertEquals Environment.DEVELOPMENT, Environment.getCurrent()

        assertEquals "dev", Environment.executeForCurrentEnvironment {
            environments {
                production {
                    "prod"
                }
                development {
                    "dev"
                }
                soe {
                    "some other environment"
                }
            }
        }

        System.setProperty("griffon.env", "soe")

        assertEquals Environment.CUSTOM, Environment.getCurrent()

        assertEquals "some other environment", Environment.executeForCurrentEnvironment {
            environments {
                production {
                    "prod"
                }
                development {
                    "dev"
                }
                soe {
                    "some other environment"
                }
            }
        }
    }

    void testGetEnvironmentSpecificBlock() {
        System.setProperty("griffon.env", "prod")

        assertEquals Environment.PRODUCTION, Environment.getCurrent()

        def callable = Environment.getEnvironmentSpecificBlock {
            environments {
                production {
                    "prod"
                }
                development {
                    "dev"
                }
                soe {
                    "some other environment"
                }
            }
        }


        assertEquals "prod", callable.call()

        System.setProperty("griffon.env", "dev")

        assertEquals Environment.DEVELOPMENT, Environment.getCurrent()

        callable = Environment.getEnvironmentSpecificBlock {
            environments {
                production {
                    "prod"
                }
                development {
                    "dev"
                }
                soe {
                    "some other environment"
                }
            }
        }

        assertEquals "dev", callable.call()



        System.setProperty("griffon.env", "soe")

        assertEquals Environment.CUSTOM, Environment.getCurrent()

        callable = Environment.getEnvironmentSpecificBlock {
            environments {
                production {
                    "prod"
                }
                development {
                    "dev"
                }
                soe {
                    "some other environment"
                }
            }
        }

        assertEquals "some other environment", callable.call()
    }



    void testGetCurrent() {
        System.setProperty("griffon.env", "prod")

        assertEquals Environment.PRODUCTION, Environment.getCurrent()

        System.setProperty("griffon.env", "dev")

        assertEquals Environment.DEVELOPMENT, Environment.getCurrent()

        System.setProperty("griffon.env", "soe")

        assertEquals Environment.CUSTOM, Environment.getCurrent()
        
    }

    void testGetEnvironment() {

        assertEquals Environment.DEVELOPMENT, Environment.getEnvironment("dev")
        assertEquals Environment.TEST, Environment.getEnvironment("test")
        assertEquals Environment.PRODUCTION, Environment.getEnvironment("prod")
        assertNull Environment.getEnvironment("doesntexist")
    }

    void testSystemPropertyOverridesMetadata() {
        Metadata.getInstance(new ByteArrayInputStream('''
griffon.env=production
'''.bytes))

        assertEquals Environment.PRODUCTION, Environment.getCurrent()

        System.setProperty("griffon.env", "dev")

        assertEquals Environment.DEVELOPMENT, Environment.getCurrent()

        System.setProperty("griffon.env", "")

        assertEquals Environment.PRODUCTION, Environment.getCurrent()

        Metadata.getInstance(new ByteArrayInputStream(''.bytes))

        assertEquals Environment.DEVELOPMENT, Environment.getCurrent()

    }
}
