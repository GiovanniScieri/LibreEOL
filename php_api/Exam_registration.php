<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if(isset($_POST['password']) && isset($_POST['idExam'])){
	
	$clientPassword = $_POST['password'];
	$idExam = $_POST['idExam'];

	$db->qSelect('Exams', 'idExam', $idExam);
	$exam = $db->nextRowAssoc();
	$password = $exam['password'];

	if($clientPassword == $password){
		$response['error'] = FALSE;
		$response['msg'] = "Password corretta";
		echo json_encode($response);
	} else {
		$response['error'] = TRUE;
		$response['error_msg'] = "Password sbagliata. Riprova";
		echo json_encode($response);
	}
} else {
	$response['error'] = TRUE;
	$response['error_msg'] = "Inserisci la password";
	echo json_encode($response);
}

?>