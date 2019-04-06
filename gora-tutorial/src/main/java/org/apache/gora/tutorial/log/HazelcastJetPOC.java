package org.apache.gora.tutorial.log;

import com.hazelcast.core.IList;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.*;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.tutorial.log.generated.Pageview;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;

public class HazelcastJetPOC {

    private static DataStore<Long, Pageview> dataStore;
    private static final String LIST_NAME = "page-views";

    public static void main(String[] args) {
        System.out.println("ok");
        try {
            dataStore = DataStoreFactory.getDataStore(Long.class, Pageview.class,
                    new Configuration());
        } catch (GoraException e) {
            e.printStackTrace();
        }

        IList<Pageview> list;
        try {
            JetInstance jet = startJet();
            list = jet.getList(LIST_NAME);
            runPipeline(jet);

            System.out.println("printing " + list.size());

            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        } finally {
            System.out.println("shutting down");
            Jet.shutdownAll();
        }
    }

    private static JetInstance startJet() {
        System.out.println("Creating Jet instance");
        return Jet.newJetInstance();
    }

    private static void runPipeline(JetInstance jet) {
        System.out.println("\nRunning the pipeline ");
        Pipeline p = buildPipeline();
        jet.newJob(p).join();
    }

    private static Pipeline buildPipeline() {
        BatchSource<Pageview> fileSource = SourceBuilder
                .batch("source", x ->
                        new PageViewProvider())
                .fillBufferFn(PageViewProvider::fillBuffer)
                .destroyFn(PageViewProvider::close)
                .build();
        Pipeline p = Pipeline.create();
        p.drawFrom(fileSource)
                .drainTo(Sinks.list(LIST_NAME));
        return p;
    }

    private static class PageViewProvider {

        private long count = 0;

        void fillBuffer(SourceBuilder.SourceBuffer<Pageview> buf) {
            System.out.println("buf");

            try {
                if (count < 40) {
                    Pageview result = dataStore.get(count);
                    buf.add(result);
                    count++;
                } else {
                    buf.close();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void close() {
            dataStore.close();
        }
    }
}
