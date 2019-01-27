<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if($db->qListGroup() && $subGroup = $db->getResultAssoc()){
	
	$i = 0;
	while($i != count($subGroup)){
		$subGroupName[$i] = $subGroup[$i]["NameSubGroup"];
		$i++;
	}

	$response['error'] = FALSE;
	$response['subGroup'] = $subGroupName;
	echo json_encode($response);
} else {
	$response['error'] = TRUE;
	$response['error_msg'] = "Impossibile listare subGroup";
	echo json_encode($response);
}

?>