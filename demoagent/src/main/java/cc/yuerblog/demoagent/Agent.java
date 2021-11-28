package cc.yuerblog.demoagent;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("demoagent启动");
        inst.addTransformer(new Transformer());
    }
}
