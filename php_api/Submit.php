<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');
require_once('/home/u949913003/public_html/questionTypes/Question.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if(isset($_POST['idSet']) && isset($_POST['questions']) && isset($_POST['answers'])){

	$idSet = $_POST['idSet'];
	$questions = json_decode($_POST['questions']);
	$answers = json_decode($_POST['answers']);

	if(($db->qTestDetails($idSet)) && ($testInfo = $db->nextRowAssoc())){
		$lang = $testInfo['fkLanguage'];

		if($db->qUpdateTestAnswers($idSet, $lang, $questions, $answers)){
			
			if($db->qEndTest($idSet)){
				$response['error'] = FALSE;
				$response['msg'] = "Test consegnato correttamente";
				$response['msg1'] = $answers;
				echo json_encode($response);
			} else {
				$response['error'] = TRUE;
				$response['error_msg'] = "Impossibile consegnare test";
				echo json_encode($response);
			}
		} else {
			$response['error'] = TRUE;
			$response['error_msg'] = "Update answer fallita";
			echo json_encode($response);
		}	
	} else{
		$response['error'] = TRUE;
		$response['error_msg'] = "Impossibile reperire dati test";
		echo json_encode($response);
	}	
} else {
	$response['error'] = TRUE;
	$response['error_msg'] = "Parametri mancanti";
	echo json_encode($response);
}

?>