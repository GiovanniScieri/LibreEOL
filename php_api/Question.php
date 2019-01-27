<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');
require_once('/home/u949913003/public_html/questionTypes/Question.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if(isset($_POST['idSet'])){

	$idSet = $_POST['idSet'];

	if(($db->qTestDetails($idSet)) && ($testInfo = $db->nextRowAssoc())){
		$lang = $testInfo['fkLanguage'];
		$subject = $testInfo['fkSubject'];

		if(($db->qQuestionSet($idSet, $lang, $subject)) && ($questions = $db->getResultAssoc())){;
		shuffle($questions);

		$i = 0;
		while($i != count($questions)){
			$questionId = $questions[$i]['idQuestion'];
			$db->qAnswerSet($questionId, $lang, $subject);
			$answer[$i] = $db->getResultAssoc();
			$questions[$i]['answers'] = $answer[$i];
			$i++;
		}
		
		$response["error"] = FALSE;
		$response["question"] = $questions;
		echo json_encode($response);
		
		} else {
			$response["error"] = TRUE;
			$response["error_msg"] = "Impossibile reperire questionSet";
			echo json_encode($response);
		}
	} else {
		$response["error"] = TRUE;
		$response["error_msg"] = "Impossibile reperire dettagli test per creare domande";
		echo json_encode($response);
	}
} else {
	$response["error"] = TRUE;
	$response["error_msg"] = "Parametri mancanti";
	echo json_encode($response);
}

?>