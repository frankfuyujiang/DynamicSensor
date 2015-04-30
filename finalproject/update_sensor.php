<?php

/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();

// check for required fields
    
$device_sensorID = $_POST['device_sensorID'];
$type = $_POST['type'];
$longitude = $_POST['longitude'];
$latitude = $_POST['latitude'];
$value=$_POST['value'];
settype($longitutde,"double");
settype($latitude,"double");
settype($value,"double");

    // include db connect class
include_once('connect.php');
    // mysql update row with matched pid
$result = mysqli_fetch_row(mysqli_query($cxn,"SELECT * FROM sensor_data WHERE device_sensorID='$device_sensorID'"));    
// check if row inserted or not
if (empty($result)) {
    // successfully updated
    $result=mysqli_query($cxn,"INSERT INTO sensor_data VALUES ('$device_sensorID', '$type', '$longitude','$latitude','$value')");
    if($result){
        $response["success"] = 1;
        $response["message"] = "Updated Sucessfully1";
    echo json_encode($response);
    }else{
        $response["success"]=0;
        $response["message"]="Updating Error, Please retry later2";
    echo json_encode($response);
    }
} else {
    $result=mysqli_query($cxn,"UPDATE sensor_data Set type='$type',longitude='$longitude',latitude='$latitude',value='$value' where device_sensorID='$device_sensorID' ");
    if($result){
        $response["success"] = 1;
        $response["message"] = "Updated Sucessfully3";

        echo json_encode($response);
    }else{
        $response["success"]=0;
        $response["message"]="Updating Error, Please retry later4";
        echo json_encode($response);
    }
}
?>
