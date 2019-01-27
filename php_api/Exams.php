<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if(isset($_POST['idSubject']) && isset($_POST['idUser'])){

	$idSubject = $_POST['idSubject'];
	$idUser = $_POST['idUser'];

	if($db->qExamsAvailable($idSubject, $idUser)){
		$i = 0;

		while($shoulder = $db->nextRowAssoc()){
			$exam[$i] = $shoulder;
			$i++;
		}

		//$exams = $db->getResultAssoc();
	
		if($exam != NULL){
			$response["error"] = FALSE;
			$response["exam"]= $exam;
			echo json_encode($response);
		} else {
			$response["error"] = TRUE;
			$response["error_msg"] = "Esami non presenti";
			echo json_encode($response);
		}
	} else {
		$response["error"] = TRUE;
		$response["error_msg"] = "Query lista esami fallita";
		echo json_encode($response);
	}
} else {
	$response["error"] = TRUE;
	$response["error_msg"] = "Parametri non passati";
	echo json_encode($response);
}

?>