<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if(isset($_POST['idExam'])){

	$idExam = $_POST['idExam'];
  
	$db->qSelect('Exams', 'idExam', $idExam);
	$exam = $db->nextRowAssoc();

	if($exam != NULL){
		$response["error"] = FALSE;
		$response["exam"] = $exam;
		echo json_encode($response);
	} else {
		$response["error"] = TRUE;
		$response["error_msg"] = "Esame non trovato";
		echo json_encode($response);
	}
} else {
	$response["error"] = TRUE;
	$response["error_msg"] = "Id esame non passato";
	echo json_encode($response);
}

?>