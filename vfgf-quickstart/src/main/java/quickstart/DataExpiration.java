package quickstart;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.gemstone.gemfire.cache.ExpirationAttributes;
import com.gemstone.gemfire.cache.Region;

/**
 * Each data region can be configured to expire entries that have not been
 * accessed recently or that have not been updated recently. You can configure
 * eviction to remove the data and its key (destruction) or just the data
 * (invalidation). Please refer to the quickstart guide for instructions on how
 * to run this example.
 * 
 * @author GemStone Systems, Inc.
 * @since 4.1.1
 */
@Component
public class DataExpiration {

	@Resource(name = "exampleRegion")
	private Region<String, String> exampleRegion;

	public void run() throws Exception {

		System.out.println("This example shows entry expiration.");
		System.out.println();

		// Get the EntryIdleTimeout setting from the region attributes
		ExpirationAttributes expirationAttr = exampleRegion.getAttributes()
				.getEntryIdleTimeout();

		System.out.println();
		System.out.println("The region \"" + exampleRegion.getFullPath()
				+ "\" is configured to");
		System.out.println(expirationAttr.getAction()
				+ " any cache entry that is idle for ");
		System.out.println(expirationAttr.getTimeout() + " seconds.");

		int idleTime = expirationAttr.getTimeout();

		System.out.println();
		System.out.println("Putting entry: key1 => value1");
		exampleRegion.put("key1", "value1");
		System.out.println("Putting entry: key2 => value2");
		exampleRegion.put("key2", "value2");
		System.out.println("Putting entry: key3 => value3");
		exampleRegion.put("key3", "value3");

		System.out.println();
		System.out.println("The cache now contains:");
		printRegionContents(exampleRegion);

		System.out.println();
		System.out
				.println("Before the idle time expiration, access two of the entries...");
		Thread.sleep((idleTime - 1) * 1000);

		// Get key1 to prevent it from expiring
		System.out.println("Getting entry: key1 => "
				+ exampleRegion.get("key1"));

		// Update key2 to prevent it from expiring
		System.out.println("Putting entry: key2 => value2000");
		System.out.println();
		exampleRegion.put("key2", "value2000");

		System.out
				.println("Next, the listener should report on an expiration action... ");
		System.out.println();

		// Sleep past expiration
		Thread.sleep((idleTime / 2) * 1000);

		System.out.println("After the expiration timeout, the cache contains:");
		printRegionContents(exampleRegion);

	}

	private static void printRegionContents(Region<?, ?> region) {
		Object[] keys = region.keySet().toArray();
		Arrays.sort(keys);

		for (Object key : keys) {
			System.out.println("    " + key + " => " + region.get(key));
		}
	}
}