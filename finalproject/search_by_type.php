<?php

/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();


include_once('connect.php');

// check for post data

$deviceid = $_GET['device_sensorID'];

$result = mysqli_query($cxn,"SELECT * FROM sensor_data WHERE device_sensorID NOT LIKE '{$deviceid}%'");

if ($result->num_rows !==0) {
        // check for empty result
    $response["success"]=1;
    $response["sensors"]=array();
    while($row=mysqli_fetch_assoc($result)){
        extract($row);
        $sensor= array();
        $sensor['device_sensorID'] = "$device_sensorID";
        $sensor['type'] = "$type";
        $sensor['longitude'] = "$longitude";
        $sensor['latitude'] = "$latitude";
        $sensor['value'] = "$Value";
        array_push($response["sensors"],$sensor);
    }

            // echoing JSON response
        echo json_encode($response);
} else {
            // no product found
        $response["success"] = 0;
        $response["message"] = "No sensor found";

            // echo no users JSON
        echo json_encode($response);
}
?>



