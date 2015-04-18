package learnZookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {
        final String address = args[0];
        final String text = args[1];

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(address, retryPolicy);
        client.start();

        LeaderSelectorListener listener = new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                while(true) {
                    System.out.println("I am a leader. I am a " + text);
                    Thread.sleep(1000 * 10);
                }
            }
        };
        LeaderSelector leaderSelector = new LeaderSelector(client, "/my_path", listener);
        leaderSelector.autoRequeue();
        leaderSelector.start();

        while(true) {
            Thread.sleep(1000 * 10);
        }
    }
}
