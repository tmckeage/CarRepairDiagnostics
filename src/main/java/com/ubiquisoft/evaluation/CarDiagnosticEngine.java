package com.ubiquisoft.evaluation;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.partitioningBy;

public class CarDiagnosticEngine {

	public void executeDiagnostics(Car car) {
		List<String> missingData = getMissingCarData(car);
		if (missingData.size() > 0){
			missingData.forEach(this::printMissingCarData);
			return;
		}

		Map<PartType, Integer> missingParts = car.getMissingPartsMap();
		if(missingParts.size() > 0){
			missingParts.forEach(this::printMissingPart);
			return;
		}

		List<Part> damagedParts = car.getParts().stream()
				.filter(p -> !p.isInWorkingCondition()).collect(Collectors.toList());
		if(damagedParts.size() > 0){
			damagedParts.forEach(p -> printDamagedPart(p.getType(), p.getCondition()));
			return;
		}

		System.out.println("Car Validation Successful");

	}

	private List<String> getMissingCarData(Car car) {
		List<String> missingData = new ArrayList<>();
		if(car.getMake() == null){
			missingData.add("Make");
		}
		if(car.getModel() == null){
			missingData.add("Model");
		}
		if(car.getYear() == null){
			missingData.add("Year");
		}
		return missingData;
	}

	private void printMissingCarData(String carData) {
		if (carData == null) throw new IllegalArgumentException("CarData must not be null");
		System.out.println(String.format("Missing Car Data Detected: %s", carData));
	}

	private void printMissingPart(PartType partType, Integer count) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (count == null || count <= 0) throw new IllegalArgumentException("Count must be greater than 0");

		System.out.println(String.format("Missing Part(s) Detected: %s - Count: %s", partType, count));
	}

	private void printDamagedPart(PartType partType, ConditionType condition) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (condition == null) throw new IllegalArgumentException("ConditionType must not be null");

		System.out.println(String.format("Damaged Part Detected: %s - Condition: %s", partType, condition));
	}

	public static void main(String[] args) throws JAXBException {
		// Load classpath resource
		InputStream xml = ClassLoader.getSystemResourceAsStream("SampleCar.xml");

		// Verify resource was loaded properly
		if (xml == null) {
			System.err.println("An error occurred attempting to load SampleCar.xml");

			System.exit(1);
		}

		// Build JAXBContext for converting XML into an Object
		JAXBContext context = JAXBContext.newInstance(Car.class, Part.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Car car = (Car) unmarshaller.unmarshal(xml);

		// Build new Diagnostics Engine and execute on deserialized car object.

		CarDiagnosticEngine diagnosticEngine = new CarDiagnosticEngine();

		diagnosticEngine.executeDiagnostics(car);

	}

}
