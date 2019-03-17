# Example for MicroProfile Config & Healthcheck

This is an example of a [MicroProfile][microprofile] application that is running with [WildFly Swarm][swarm]

## Description of the application

This application is a simple Web Service that returns a list of random integers.

When an users calls `http://localhost:8080/`, it will receive a list of random integers in a JSON array:

```
$ curl  http://localhost:8080/
[630030638,1689535734,1995796600]
```

The application uses [MicroProfile Config][mp-config] to configure its behaviour.
It defines two configuration properties:

* `num_size` - the number of generated random integers (3 by default)
* `num_max` - the maximum value of integer (`Integer.MAX_VALUE` by default)

These 2 properties are configured using the Eclipse MicroProfile Config API in the NumbersGenerator class.

```
@Inject
@ConfigProperty(name = "num_size", defaultValue = "3")
private int numSize;

@Inject
@ConfigProperty(name = "num_max", defaultValue = "" + Integer.MAX_VALUE)
private int numMax;
```

## Build the application

```
$ mvn clean package
```

## Run the standalone application

Let's run the application as a standalone UberJar and uses the default configuration:

```
$ java -jar target/numbers-swarm.jar
...
2018-01-15 11:58:50,658 INFO  [org.jboss.as.server] (main) WFLYSRV0010: Deployed "numbers.war" (runtime-name : "numbers.war")
2018-01-15 11:58:50,677 INFO  [org.wildfly.swarm] (main) WFSWARM99999: WildFly Swarm is Ready
```

* We can go repeatedly to http://localhost:8080/ to generate list of random integers

```
[1138457167,1094692586,668205483]
[97438221,1975418940,665179314]
[1642296265,44689490,105581874]
```

### Configuring the application

We can now use System properties to change the configuration of the application to generate `5` (configured with `num_size`) integers
 between `0` and `10` (configured with `num_max`).

```
$ java -jar target/numbers-swarm.jar -Dnum_size=5 -Dnum_max=10
...
2018-01-15 12:01:18,345 INFO  [org.jboss.as.server] (main) WFLYSRV0010: Deployed "numbers.war" (runtime-name : "numbers.war")
2018-01-15 12:01:18,357 INFO  [org.wildfly.swarm] (main) WFSWARM99999: WildFly Swarm is Ready
```

* If we now go repeatedly to http://localhost:8080/, we see that the list of random integers has 5 elements between 0 and 10:

```
[2,5,3,3,0]
[3,0,6,2,2]
[9,4,4,9,1]
```

### Determine the healthiness of the application

This application is trivial enough that its healthiness can be determined when it is started and we could shut it down
if it is not the case.

However to illustrate the use of [MicroProfile Healthcheck][mp-healthcheck], we will use health check probes to determine the overall healthiness of the application.

The application defines 1 probe:

* `numbers.config` is a probe that checks that the configuration of `NumbersGenerator` is correct.

The `numbers.config` probe is provided by the `ConfigHealthCheck` class. When this class is called, it verifies that the configuration of `NumbersGenerator` is correct.
If it is correct, it returns `UP`. Otherwise it returns `DOWN` (as calls to `NumbersGenerator.nextInts()` will fail).

The healthiness of the application can be checked by calling the `http://localhost:8080/health` endpoint that is provided automatically by WildFly Swarm
(the application does not provide this `/health` endpoint).

```
$ curl -v  http://localhost:8080/health
...
< HTTP/1.1 200 OK
...
{"checks": [
{"name":"numbers.config","state":"UP"}],
"outcome": "UP"
}
```

The `200 OK` response indicates that the overall healthiness of the application is `UP` (using the `outcome` property).
It also lists the outcome of individual probes. In our case, the single `numbers.config` probe is `UP`.

Let's now start the application with an *invalid* configuration by setting `num_max` to `-10`:

```
$ java -jar target/numbers-swarm.jar -Dnum_max=-10
...
2018-01-15 12:23:16,951 INFO  [org.jboss.as.server] (main) WFLYSRV0010: Deployed "numbers.war" (runtime-name : "numbers.war")
2018-01-15 12:23:16,966 INFO  [org.wildfly.swarm] (main) WFSWARM99999: WildFly Swarm is Ready
```

If we call the Web service, we get an error:

```
$ curl -v  http://localhost:8080/
...
< HTTP/1.1 500 Internal Server Error
...
Caused by: java.lang.IllegalArgumentException: bound must be greater than origin
...
```

If we call the `/health` endpoint, it indicates that the application is indeed not healthy:

```
$ curl -v  http://localhost:8080/health
...
< HTTP/1.1 503 Service Unavailable
...
{"checks": [
{"name":"numbers.config","state":"DOWN","data": {"num_size":3,"num_max":-10}}],
"outcome": "DOWN"
}
```

The `503 Service Unavailable` response indicates that the application is not healthy.
The list of `checks` indicates that the `numbers.config` probe is `DOWN`.
This probe includes `data` that helps identify why the check fails. We can see here that it is due to an invalid `num_max` value.

## Deploy the application on OpenShift

* Sign up at [OpenShift][openshift]

```
$ oc login
$ oc status
$ mvn -Popenshift clean fabric8:deploy
$ oc get routes/microprofile-openshift-example
NAME                             HOST/PORT                                                      PATH      SERVICES                         PORT      TERMINATION   WILDCARD
microprofile-openshift-example   microprofile-openshift-example-myproject.192.168.64.4.nip.io             microprofile-openshift-example   8080                    None
```


### Configure environment variables in OpenShift

```
$ oc env dc/microprofile-openshift-example --list
# deploymentconfigs microprofile-openshift-example, container wildfly-swarm
# KUBERNETES_NAMESPACE from field path metadata.namespace

$ oc env dc/microprofile-openshift-example num_size=5 num_max=10

$ oc env dc/microprofile-openshift-example --list
# deploymentconfigs microprofile-openshift-example, container wildfly-swarm
# KUBERNETES_NAMESPACE from field path metadata.namespace
num_size=5
num_max=10
```

### Configure the readiness probe

The readiness probe is automatically configured in OpenShift to use the `/health` endpoint of our application by the `fabric8` Maven plugin is configured in the `pom.xml`:

```
<plugin>
  <groupId>io.fabric8</groupId>
  <artifactId>fabric8-maven-plugin</artifactId>
  ...
  <configuration>
    ...
    <enricher>
      <config>
        <wildfly-swarm-health-check>
          <path>/health</path>
        </wildfly-swarm-health-check>
      </config>
    </enricher>
  </configuration>
</plugin>
```


[microprofile]: https://microprofile.io
[swarm]: http://wildfly-swarm.io
[mp-config]: https://github.com/eclipse/microprofile-config
[mp-healthcheck]: https://github.com/eclipse/microprofile-health
[openshift]: https://www.openshift.com
