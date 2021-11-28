package cc.yuerblog.helloworld;

public class Main {
    public static void main(String[] args) {
        System.out.println("主程序启动");

        SomeClass sc = new SomeClass();
        sc.echo("正常调用");
    }
}
