<?php
require 'connection.php';
define('PATH', 'http://'.$_SERVER['HTTP_HOST'].'/test/images/');
define('NO_IMAGE_PATH', PATH.'no_image.png');
$action = $_REQUEST['action'];
/*$header = apache_request_headers();
if ($header['user']=='admin' && $header['password']=='admin') {
        
}else{
        $jsonOutput['meta']['status']  = 'error';
        $jsonOutput['meta']['code']    = '200';
        $jsonOutput['meta']['message'] = 'You are not authorized to do.';        
        header('Content-type: application/json');
        echo json_encode($jsonOutput);
        exit;
}*/

switch ($action) {
	case 'login':
         if ($_SERVER['REQUEST_METHOD'] == 'POST') {
                 login($_REQUEST,$con);
         }else{
                $jsonOutput['meta']['status']  = 'error';
                $jsonOutput['meta']['code']    = '200';
                $jsonOutput['meta']['message'] = 'Invalid Action';        
                header('Content-type: application/json');
                echo json_encode($jsonOutput);
         }
		
	break;

	case 'register':
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {
                 register($_REQUEST,$con);
         }else{
                $jsonOutput['meta']['status']  = 'error';
                $jsonOutput['meta']['code']    = '200';
                $jsonOutput['meta']['message'] = 'Invalid Action';        
                header('Content-type: application/json');
                echo json_encode($jsonOutput);
         }
		
	break;
			
	default:	
		$jsonOutput['meta']['status']  = 'error';
                $jsonOutput['meta']['code']    = '200';
                $jsonOutput['meta']['message'] = 'Invalid Action';        
                header('Content-type: application/json');
                echo json_encode($jsonOutput);
                exit;
        break;
}

function login($request,$con)
{
        
        $mobile = $request['mobile'];
        $password = $request['password'];

        $msg = '';
        if (empty($mobile)) {
                $msg='Please enter mobile number.';
        }elseif (empty($password)) {
                $msg='Please enter password.';
        }
        if (empty($msg)) {
                $password = md5($password);
                $result = mysqli_query($con,"SELECT id,name,mobile,email,user_image FROM users where mobile=".$mobile." and password='".$password."'");
                if ($result->num_rows>0) {
                        

                        $row = mysqli_fetch_assoc($result);
                        if(file_exists("images/".$row['user_image'])){
                                $row['user_image'] = PATH.$row['user_image'];
                        }else{
                              $row['user_image'] = NO_IMAGE_PATH;  
                        }
                        $jsonOutput['meta']['status']  = 'success';
                        $jsonOutput['meta']['code']    = '200';
                        $jsonOutput['meta']['message'] = 'Login Successfully';
                        $jsonOutput['data'] = $row;

                        header('Content-type: application/json');
                        echo json_encode($jsonOutput);
                        exit;
                }else
                {
                        $resultMobile = mysqli_query($con,"SELECT id FROM users where mobile=".$mobile);
                        $resultPassword = mysqli_query($con,"SELECT id FROM users where password='".$password."'");
                        if ($resultMobile->num_rows <= 0 && $resultPassword->num_rows <= 0) {
                                $msg = "Invalid mobile number and password.";
                        }elseif ($resultMobile->num_rows <= 0) {
                                $msg = "Invalid mobile number.";
                        }elseif ($resultPassword->num_rows <= 0) {
                                $msg = "Invalid password.";
                        }
                        $jsonOutput['meta']['status']  = 'error';
                        $jsonOutput['meta']['code']    = '200';
                        $jsonOutput['meta']['message'] = $msg;
                        $jsonOutput['data'] = (object)array();

                        header('Content-type: application/json');
                        echo json_encode($jsonOutput);
                        exit; 
                }

        }else{
                $jsonOutput['meta']['status']  = 'error';
                $jsonOutput['meta']['code']    = '200';
                $jsonOutput['meta']['message'] = $msg;
                $jsonOutput['data'] = (object)array();
                header('Content-type: application/json');
                echo json_encode($jsonOutput);
                exit;   
        }
 }
function register($request,$con)
{       
        $name = $request['name'];
        $mobile = $request['mobile'];
        $email = $request['email'];
        $password = $request['password'];
        $file = $_FILES['user_image'];
        $msg = '';
        if (empty($file)) {
                $msg='Please upload your image.';
        }elseif (empty($name)) {
                $msg='Please enter name.';
        }elseif (empty($mobile)) {
                $msg='Please enter mobile number.';
        }elseif (empty($email)) {
                $msg='Please enter email address.';
        }elseif (empty($password)) {
                $msg='Please enter password.';
        }
        if (empty($msg)) {
                $resultMobile = mysqli_query($con,"SELECT id FROM users WHERE mobile=".$mobile);
                $resultEmail = mysqli_query($con,"SELECT id FROM users WHERE email='".$email."'");
                $msg = '';
                if ($resultMobile->num_rows > 0 && $resultEmail->num_rows > 0) {
                        $msg = "Mobile number and email address already exists.";
                }elseif ($resultMobile->num_rows > 0) {
                        $msg = "Mobile number already exists.";
                }elseif ($resultEmail->num_rows > 0) {
                        $msg = "Email address already exists.";
                }
                if (empty($msg)) {
                        $user_image = $file['name'];
                        $ext = explode('.', $user_image);
                        $extention = $ext[count($ext)-1];

                        $password = md5($password);
                $query = "INSERT INTO users (name,mobile,email,password,user_image) VALUES('".$name."','".$mobile."','".$email."','".$password."','".$user_image."')";
                        $insert = mysqli_query($con,$query);
                        $insert_id = $con->insert_id;
                        $file_name = "user_image_".$insert_id.".".$extention;
                        $target = "images/".$file_name;
                        move_uploaded_file($_FILES['user_image']['tmp_name'], $target);
                        $update = mysqli_query($con,"UPDATE users SET user_image='".$file_name."' where id=".$insert_id);
                        $result = mysqli_query($con,"SELECT id,name,mobile,email,user_image FROM users where id=".$insert_id);
                        $row = mysqli_fetch_assoc($result);
                        if(file_exists("images/".$row['user_image'])){
				//chmod("images/".$row['user_image'], 0777);

                                $row['user_image'] = PATH.$row['user_image'];
                        }else{
                              $row['user_image'] = NO_IMAGE_PATH;  
                        }
                        $jsonOutput['meta']['status']  = 'success';
                        $jsonOutput['meta']['code']    = '200';
                        $jsonOutput['meta']['message'] = 'Registration successfully.';
                        $jsonOutput['data'] = (object)$row;
                        header('Content-type: application/json');
                        echo json_encode($jsonOutput);
                        exit;
                }else{
                        $jsonOutput['meta']['status']  = 'error';
                        $jsonOutput['meta']['code']    = '200';
                        $jsonOutput['meta']['message'] = $msg;
                        $jsonOutput['data'] = (object)array();
                        header('Content-type: application/json');
                        echo json_encode($jsonOutput);
                        exit;
                }
	}else{
                $jsonOutput['meta']['status']  = 'error';
                $jsonOutput['meta']['code']    = '200';
                $jsonOutput['meta']['message'] = $msg;
                $jsonOutput['data'] = (object)array();
                header('Content-type: application/json');
                echo json_encode($jsonOutput);
                exit;   
        }
}
?>
