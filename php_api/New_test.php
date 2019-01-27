<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');
require_once('/home/u949913003/public_html/includes/essential.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if(isset($_POST['idExam']) && isset($_POST['idUser'])){

	$idExam = $_POST['idExam'];
	$idUser = $_POST['idUser'];

	$db->qSelectTwoArgs('Tests', 'fkExam', $idExam, 'fkUser', $idUser);
	$control = $db->nextRowAssoc();

	if($control == NULL){
		$result = $db->qMakeQuestionsSet($idExam, $idUser);
		$db->qSelectTwoArgs('Tests', 'fkExam', $idExam, 'fkUser', $idUser);
		$test = $db->nextRowAssoc();
		$idTest = $test['idTest'];

		if($result){
			$response["error"] = FALSE;
			$response["idTest"] = $idTest;
			echo json_encode($response);
		} else {
			$response["error"] = TRUE;
			$response["error_msg"] = "Query creazione test fallita";
			echo json_encode($response);
		}
	} elseif($control["timeEnd"] == NULL) {
		$db->qSelectTwoArgs('Tests', 'fkExam', $idExam, 'fkUser', $idUser);
		$test = $db->nextRowAssoc();
		$idTest = $test['idTest'];
		$response["error"] = TRUE;
		$response["idTest"] = $idTest;
		$response["error_msg"] = "Sei già iscritto all'esame. Accedi";
		echo json_encode($response);
	} else {
		$response["error"] = TRUE;
		$response["error_msg"] = "Esame già sostenuto";
		echo json_encode($response);
	}

} else {
	$response["error"] = TRUE;
	$response["error_msg"] = "Parametri non passati";
	echo json_encode($response);
}

?>