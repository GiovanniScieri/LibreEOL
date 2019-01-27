<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if(isset($_POST['idTest'])){

	$idTest = $_POST['idTest'];

	$db->qSelect('Tests', 'idTest', $idTest);
	$array = $db->nextRowAssoc();
	$idExam = $array['fkExam'];
	$idUser = $array['fkUser'];
	$idSet = $array['fkSet'];

	if($idSet == NULL){
		if(($db->qAssignSet($idExam, $idUser)) && ($Set = $db->nextRowEnum())){
		$idSet = $Set[0];
		}
	}

	if(($db->qTestDetails($idSet)) && ($testInfo = $db->nextRowAssoc())){

		$lang = $testInfo['fkLanguage'];
		$subject = $testInfo['fkSubject'];
		$questionsNum = $testInfo['questions'];
		$timeStart = $testInfo['timeStart'];
		$timeEnd = $testInfo['timeEnd'];
		$now = date("Y-m-d H:i:s");
		$remaining = $duration = $testInfo['duration'] * 60; //seconds

		switch($testInfo['status']){
			case 'e' :
			case 'a' :  // This test has been already submitted, so don't load questions and exit
				$response['error'] = TRUE;
				$response['error_msg'] = "Test gia eseguito";
				echo json_encode($response);
				break;
			case 'b' :  // This test has been blocked, so don't load questions and exit
				$response['error'] = TRUE;
				$response['error_msg'] = "Test bloccato";
				echo json_encode($response);
				break;
			case 's' :  // This test was already opened (status = s), check remaining time
				$timeStart = strtotime($testInfo['timeStart']);
				$now = strtotime($now);
				$used = $now - $timeStart;
				if($used > $duration){
					$response['error'] = TRUE;
					$response['error_msg'] = "Tempo scaduto";
					echo json_encode($response); 
				} else {
					$remaining = $duration - $used;
					$response['error'] = FALSE;
					$response['idSet'] = $idSet;
					$response['remaining'] = $remaining;
					$response['msg'] = "Test gia avviato";
					echo json_encode($response); 
				}
				break;
			case 'w' :  // Opening test for the first time, so set timeStart, status and load questions
				if($db->qStartTest($idTest, $now)){
					$response['error'] = FALSE;
					$response['idSet'] = $idSet;
					$response['duration'] = $duration;
					$response['msg'] = "Test avviato con successo";
					echo json_encode($response);
				} else {
					$response['error'] = TRUE;
					$response['error_msg'] = "Impossibile avviare il test";
					echo json_encode($response);
				}  
				break;                    
		}
	} else {
		$response['error'] = TRUE;
		$response['error_msg'] = "Impossibile reperire info test";
		echo json_encode($response);
	}
} else {
	$response['error'] = TRUE;
	$response['error_msg'] = "Id test non passato";
	echo json_encode($response);
}

?>