package net.mmly.openminemap.http;

import com.google.gson.Gson;
import net.minecraft.text.Text;
import net.mmly.openminemap.search.SearchBoxLayer;
import net.mmly.openminemap.search.SearchResult;
import net.mmly.openminemap.search.SearchResultType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public abstract class LocationJsonRequester<T> extends Requester<T> {
    public LocationJsonRequester(String requestThreadType) {
        super(false, 100, requestThreadType);
    }

    public final SearchResult[] getErrorResult() {
        return (new SearchResult[] {
                new SearchResult(
                        SearchResultType.LOCATION,
                        Double.NaN,
                        Double.NaN,
                        false,
                        "",
                        Text.translatable("omm.notification.something-wrong").getString(),
                        0
                )
        });
    }

    public final SearchResult[] getBlankResult() {
        return (new SearchResult[] {
                new SearchResult(
                        SearchResultType.LOCATION,
                        Double.NaN,
                        Double.NaN,
                        false,
                        "",
                        Text.translatable("omm.search.no-results").getString(),
                        0
                )
        });
    }

    protected final SearchResult[] parseLocationJson(InputStream stream) {
        Gson gson = new Gson();
        ArrayList<SearchResult> results = new ArrayList<>();
        Map returnedResult;

        try {
            returnedResult = gson.fromJson(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)), Map.class);
        } catch (NullPointerException e) {
            return null;
        }

        try {
            if (!returnedResult.get("type").equals("FeatureCollection")) return null;
        } catch (NullPointerException e) {
            return null;
        }

        ArrayList features = (ArrayList) returnedResult.get("features");

        for (int i = 0; i < features.size(); i++) {
            Map feature = (Map) (features.get(i));
            Map geometry = (Map) feature.get("geometry");
            Map properties = (Map) feature.get("properties");
            ArrayList coords = (ArrayList) geometry.get("coordinates");

            String context = "";
            //if (properties.get("county") != null) context += properties.get("county") + ", ";
            if (properties.get("city") != null) context += properties.get("city") + ", ";
            if (properties.get("state") != null) context += properties.get("state") + ", ";
            if (properties.get("country") != null) context += properties.get("country") + ", ";
            if (!context.isEmpty()) context = context.substring(0, context.length() - 2);

            ArrayList extentList = (ArrayList) properties.get("extent");
            double[] extent = null;
            if (extentList != null) extent = new double[] {
                    (double) extentList.get(1),
                    (double) extentList.get(3),
                    (double) extentList.get(0),
                    (double) extentList.get(2)
            };

            results.add(new SearchResult(
                    SearchResultType.LOCATION,
                    (Double) coords.get(1),
                    (Double) coords.get(0),
                    false,
                    (String) properties.get("name"),
                    context,
                    extent
            ));
        }

        SearchResult[] results1 = new SearchResult[Math.min(SearchBoxLayer.MAX_RESULTS, results.size())];
        for (int i = 0; i < results1.length; i++) {
            results1[i] = results.get(i);
        }

        return results1;
    }
}
