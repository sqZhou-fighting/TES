package top.jach.tes.plugin.tes.code.git.commit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.fastjson.parser.Feature.DisableFieldSmartMatch;
import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;

/**
 * @Author: zhoushiqi
 * @date: 2020/6/15
 */
public class DiffFileMongoRepository implements DiffFileRepository {
    public static final String COMMIT_REPOS_ID = "reposId";
    public static final String COMMIT_REPO_NAME = "repoName";
    public static final String COMMIT_SHAS = "_commit_shas";

    MongoCollection diffFileCollection;

    public DiffFileMongoRepository(MongoCollection diffFileCollection){
        this.diffFileCollection = diffFileCollection;

    }

    @Override
    public ObjectId saveDiffFile(DiffFile diffFile, Long reposId, String repoName, String sha) {
        if (diffFile != null){
            try {
                Document document = Document.parse(JSONObject.toJSONString(diffFile, DisableCircularReferenceDetect))
                        .append(COMMIT_REPOS_ID, reposId)
                        .append(COMMIT_REPO_NAME, repoName)
                        .append(COMMIT_SHAS, sha);
                diffFileCollection.insertOne(document);
                return (ObjectId)document.get("_id");
            }catch (Exception e){
                System.out.println("存储DiffFile失败： reposId:" + reposId + "  repoName:" + repoName + "  SHA" + sha);
            }
        }
        return null;
    }

    @Override
    public void saveDiffFiles(List<DiffFile> diffFiles, Long reposId, String repoName, String sha){
//        synchronized (reposId + "&&" + repoName + "&&" + sha){
            List<Document> documents = new ArrayList<>();
            for (DiffFile diffFile : diffFiles){
                if (diffFile == null){
                    continue;
                }
                Document document = Document.parse(JSONObject.toJSONString(diffFile, DisableCircularReferenceDetect))
                        .append(COMMIT_REPOS_ID, reposId)
                        .append(COMMIT_REPO_NAME, repoName)
                        .append(COMMIT_SHAS, sha);
                documents.add(document);
            }
            if(documents.size()>0) {
                try {
                    diffFileCollection.insertMany(documents);
                }catch (Exception e){
                    System.out.println("存储DiffFiles失败： reposId:" + reposId + "  repoName:" + repoName + "  SHA" + sha);
                }
            }
//        }
    }

    @Override
    public List<DiffFile> findDiffFilesBySHA(String repoId, String repoName, String sha){
        FindIterable<Document> documents = diffFileCollection.find(Filters.and(Filters.eq(COMMIT_REPOS_ID, repoId),
                Filters.eq(COMMIT_REPO_NAME, repoName),
                Filters.eq(COMMIT_SHAS, sha)));
        List<DiffFile> diffFiles = new ArrayList<>();
        for (Document document : documents){
            DiffFile diffFile = (DiffFile) JSON.parseObject(document.toJson(JsonWriterSettings.builder().build()),
                    DiffFile.class, DisableFieldSmartMatch);
            diffFiles.add(diffFile);
        }
        return diffFiles;
    }

    @Override
    public DiffFile findDiffFileById(ObjectId id){
        FindIterable<Document> documents = diffFileCollection.find(Filters.and(Filters.eq("_id", id)));
        DiffFile diffFile = null;
        for (Document document : documents){
            diffFile = JSON.parseObject(document.toJson(JsonWriterSettings.builder().build()),
                    DiffFile.class, DisableFieldSmartMatch);
            if (diffFile == null){
                System.out.println("查询DiffFile失败！");
            }
        }
        return diffFile;
    }

}
