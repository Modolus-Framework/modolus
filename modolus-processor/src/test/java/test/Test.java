package test;

import com.modolus.annotations.singleton.CreateOnRuntime;
import com.modolus.annotations.singleton.SingletonFor;

import java.util.Comparator;

@SingletonFor(Test.class)
@SingletonFor(Comparator.class)
@CreateOnRuntime
public class Test extends AbstractTest implements Comparator<Test> {
    @Override
    public int compare(Test o1, Test o2) {
        return 0;
    }
}
