# Sensor Monitoring System

A reactive, coroutine-based system designed to monitor environmental sensors (Temperature and Humidity) via UDP. The
application listens on specific ports, parses incoming sensor data, and triggers alarms if thresholds are exceeded.

---

## Technologies Used

* **Language**: Kotlin 2.1+
* **Concurrency**: Kotlin Coroutines & Flow (Reactive streams)
* **Networking**: Java DatagramSocket (UDP)
* **Testing**:
    * **JUnit 5**: Test runner
    * **MockK**: Mocking and Spying framework
    * **Kotlinx Coroutines Test**: Structured concurrency testing
* **Logging**: Custom Console Logger with timestamping and stream separation (stdout/stderr)

---

## How to Run the Project

### Prerequisites

* Java 21 or higher
* Maven 3.9+

### Steps

1. **Build the project** using Maven:
   ```bash
   mvn clean install
   ```
2. **Run the application**:
   Execute the `main` method in `Application.kt`.
   OR **run the below command**:
      ```bash 
       java -jar target/sensor-monitoring-service-1.0-SNAPSHOT.jar
      ```

---

## Simulating Sensors

You can send network packets manually using `netcat` (nc) to see the system in action:

**Temperature Sensor (Port 3344, Threshold 35°C):**

```bash
echo "sensor_id=t1; value=30" | nc -u -w0 127.0.0.1 3344
echo "sensor_id=t1; value=35" | nc -u -w0 127.0.0.1 3344
echo "sensor_id=t1; value=40" | nc -u -w0 127.0.0.1 3344
echo ";value=50" | nc -u -w0 127.0.0.1 3344
echo ";sensor_id=t1;" | nc -u -w0 127.0.0.1 3344
echo "name=Temperature;sensor_id=t1;value=35" | nc -u -w0 127.0.0.1 3344
```

**Humidity Sensor (Port 3355, Threshold 50%):**

```bash
echo "sensor_id=h1; value=40" | nc -u -w0 127.0.0.1 3355
echo "sensor_id=h1; value=50" | nc -u -w0 127.0.0.1 3355
echo "sensor_id=h1; value=60" | nc -u -w0 127.0.0.1 3355
echo ";value=50" | nc -u -w0 127.0.0.1 3355
echo ";sensor_id=h1;" | nc -u -w0 127.0.0.1 3355
echo "name=Humidity;sensor_id=h1;value=45" | nc -u -w0 127.0.0.1 3355
```

---

## Load testing

Generate the events.txt file and prepare the events

```bash
for i in {1..100}; do
  if ((RANDOM % 2)); then
    echo "sensor_id=t1; value=$((RANDOM%60))"
  else
    echo "sensor_id=h1; value=$((RANDOM%100))"
  fi
done > events.txt
```

and send the events

```bash
while read -r line; do
  case "$line" in
    *t1*) echo "$line" | nc -u -w0 127.0.0.1 3344 ;;
    *h1*) echo "$line" | nc -u -w0 127.0.0.1 3355 ;;
  esac
done < events.txt
```
