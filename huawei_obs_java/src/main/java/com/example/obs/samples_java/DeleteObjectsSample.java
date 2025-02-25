/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.example.obs.samples_java;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.DeleteObjectsRequest;
import com.obs.services.model.DeleteObjectsResult;
import com.obs.services.model.DeleteObjectsResult.DeleteObjectResult;
import com.obs.services.model.DeleteObjectsResult.ErrorResult;
import com.obs.services.model.KeyAndVersion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This sample demonstrates how to delete objects under specified bucket 
 * from OBS using the OBS SDK for Java.
 * huaweiCloud:
 *   obs:
 *    endPoint: obs.cn-south-1.myhuaweicloud.com
 *    path: https://transwai-dev.obs.cn-south-1.myhuaweicloud.com/
 *   #  path: https://transwai-prod.obs.cn-south-1.myhuaweicloud.com/
 *    ak: 2CNBVV3R4B1H2JZ6AHAT
 *    sk: g9nMiK6vFqTwxGQfUkUyE9J5DO2gaqhJT8vh4HqX
 *   bucketName: transwai-dev
 *   # bucketName: transwai-prod
 */
public class DeleteObjectsSample {
    private static final String endPoint = "obs.cn-south-1.myhuaweicloud.com";
    private static final String ak = "2CNBVV3R4B1H2JZ6AHAT";
    private static final String sk = "g9nMiK6vFqTwxGQfUkUyE9J5DO2gaqhJT8vh4HqX";
    private static ObsClient obsClient;
    private static String bucketName = "transwai-dev";

    public static void main(String[] args)
            throws IOException {

        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        try {

            /*
             * Constructs a obs client instance with your account for accessing OBS
             */
            obsClient = new ObsClient(ak, sk, config);

            /*
             * Create bucket
             */
            System.out.println("Create a new bucket for demo\n");
            obsClient.createBucket(bucketName);

            /*
             * Batch put objects into the bucket
             */
            final String content = "Thank you for using Object Storage Service";
            final String keyPrefix = "MyObjectKey";
            List<String> keys = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                String key = keyPrefix + i;
                InputStream instream = new ByteArrayInputStream(content.getBytes());
                obsClient.putObject(bucketName, key, instream, null);
                System.out.println("Succeed to put object " + key);
                keys.add(key);
            }
            System.out.println();

            /*
             * Delete all objects uploaded recently under the bucket
             */
            System.out.println("\nDeleting all objects\n");

            DeleteObjectsRequest request = new DeleteObjectsRequest();
            request.setBucketName(bucketName);
            request.setQuiet(false);

            KeyAndVersion[] kvs = new KeyAndVersion[keys.size()];
            int index = 0;
            for (String key : keys) {
                kvs[index++] = new KeyAndVersion(key);
            }

            request.setKeyAndVersions(kvs);

            System.out.println("Delete results:");

            DeleteObjectsResult deleteObjectsResult = obsClient.deleteObjects(request);
            for (DeleteObjectResult object : deleteObjectsResult.getDeletedObjectResults()) {
                System.out.println("\t" + object);
            }

            System.out.println("Error results:");

            for (ErrorResult error : deleteObjectsResult.getErrorResults()) {
                System.out.println("\t" + error);
            }

            System.out.println();

        } catch (ObsException e) {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code:       " + e.getErrorCode());
            System.out.println("Request ID:      " + e.getErrorRequestId());
            System.out.println("Host ID:           " + e.getErrorHostId());
        } finally {
            if (obsClient != null) {
                try {
                    /*
                     * Close obs client
                     */
                    obsClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
