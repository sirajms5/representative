<?php

    include_once "header.php";
    require_once '../../vendor/autoload.php';    
    
    use Dotenv\Dotenv;
    $dotenv = Dotenv::createImmutable(dirname(__DIR__, 2));

    $dotenv->load();
    $serverName = $_ENV['DB_SERVER_NAME'];
    $databaseName = $_ENV['DB_NAME'];
    $userName = $_ENV['DB_USERNAME'];
    $password = $_ENV['DB_PASSWORD'];
    try{
        $conn = mysqli_connect($serverName,$userName,$password,$databaseName);
    } catch (Exception $exception) {
        error_log("Error in conn.php: " . $exception->getMessage() . "\n", 3, "./logs/errors-log.log");
    }
    

    function getData($field) {
        if (!isset($_POST[$field])) {
            $data = "";
        }
        else {
            $data = $_POST[$field];
        }

        return $data;
    }
    
?>