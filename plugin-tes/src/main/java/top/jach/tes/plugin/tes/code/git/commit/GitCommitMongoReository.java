package top.jach.tes.plugin.tes.code.git.commit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.util.*;

import static com.alibaba.fastjson.parser.Feature.DisableFieldSmartMatch;
import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;

public class GitCommitMongoReository implements GitCommitRepository {
    public static final String COMMIT_REPOS_ID = "reposId";
    public static final String COMMIT_REPO_NAME = "repoName";
    public static final String GIT_COMMIT_DATA_STRUCT_VERSION = "_data_struct_version";
    public static final String DIFF_FILE_IDS = "diffFileIds";

    MongoCollection collection;
    DiffFileRepository diffFileRepository;

    public GitCommitMongoReository(MongoCollection collection, DiffFileRepository diffFileRepository) {
        this.collection = collection;
        this.diffFileRepository = diffFileRepository;
        collection.createIndex(Indexes.ascending(COMMIT_REPOS_ID, COMMIT_REPO_NAME, "sha"));
    }

    @Override
    public void saveGitCommits(List<GitCommit> gitCommits, Long reposId, String repoName) {
        synchronized (reposId + "&&" + repoName) {
            Set<String> shas = findShasByRepo(reposId, repoName);
            List<Document> documents = new ArrayList<>();
            for (GitCommit gitCommit :
                    gitCommits) {
                if (gitCommit == null) {
                    continue;
                }
                assert reposId == gitCommit.getReposId();
                assert repoName == gitCommit.getRepoName();
                if (shas.contains(gitCommit.getSha())) {
                    continue;
                }
                List<String> diffFileIds = new ArrayList<>();
                for (DiffFile diffFile : gitCommit.getDiffFiles()) {
                    ObjectId temp = diffFileRepository.saveDiffFile(diffFile,
                            gitCommit.getReposId(),
                            gitCommit.getRepoName(),
                            gitCommit.getSha());
                    diffFileIds.add(temp.toString());
                }

//                diffFileRepository.saveDiffFiles(gitCommit.getDiffFiles(), gitCommit.getReposId(), gitCommit.getRepoName(), gitCommit.getSha());
//                List<ObjectId> diffFileIds = new ArrayList<>();
//                for (DiffFile diffFile: gitCommit.getDiffFiles()){
//                    diffFileIds.add(diffFile.getId());
//                }
                Document document = Document.parse(JSONObject.toJSONString(gitCommit, DisableCircularReferenceDetect))
                        .append(DIFF_FILE_IDS, diffFileIds)
                        .append(COMMIT_REPOS_ID, reposId)
                        .append(COMMIT_REPO_NAME, repoName)
                        .append(GIT_COMMIT_DATA_STRUCT_VERSION, GitCommit._data_struct_version);
                documents.add(document);
            }
            if (documents.size() > 0) {
                try {
                    collection.insertMany(documents);
                } catch (Exception e) {
                    System.out.println("存储GitCommit失败： reposId:" + reposId + "  repoName:" + repoName);
                }
            }
        }
    }

    @Override
    public Set<String> findShasByRepo(Long reposId, String repoName) {
        Set<String> shas = new HashSet<>();
        Iterable<Document> commitDocuments = collection.find(
                Filters.and(Filters.eq(COMMIT_REPOS_ID, reposId),
                        Filters.eq(COMMIT_REPO_NAME, repoName),
                        Filters.eq(GIT_COMMIT_DATA_STRUCT_VERSION, GitCommit._data_struct_version)))
                .projection(Projections.include("sha"));
        deleteOldVersionData(reposId, repoName);
        for (Document d :
                commitDocuments) {
            String sha = d.getString("sha");
            if (sha != null) {
                shas.add(sha);
            }
        }
        return shas;
    }

    @Override
    public Iterable<GitCommit> findByRepoAndShas(Long reposId, String repoName, Iterable<String> shas) {

        Iterable<Document> documents = collection.find(Filters.and(Filters.eq(COMMIT_REPOS_ID, reposId),
                Filters.eq(COMMIT_REPO_NAME, repoName),
                Filters.eq(GIT_COMMIT_DATA_STRUCT_VERSION, GitCommit._data_struct_version),
                Filters.in("sha", shas)));
        List<GitCommit> gitCommits = new ArrayList<>();
        for (Document document :
                documents) {
            List<ObjectId> diffFileIds = new ArrayList<>();
            if (document.get("diffFileIds").toString() != null) {
                String tmp = document.get("diffFileIds").toString();
                String[] oIarray = tmp.substring(1, tmp.length()-1).split(", ");
                for (String s : oIarray){
                    if (ObjectId.isValid(s)){
                        diffFileIds.add(new ObjectId(s));
                    }
                }
            }
            GitCommit commit = (GitCommit) JSON.parseObject(document.toJson(JsonWriterSettings.builder().build()),
                    GitCommit.class, DisableFieldSmartMatch);
            List<DiffFile> diffFiles = new ArrayList<>();
            for (ObjectId objectId : diffFileIds) {
                diffFiles.add(diffFileRepository.findDiffFileById(objectId));
            }
            commit.setDiffFiles(diffFiles);
            gitCommits.add(commit);
        }
        return gitCommits;
    }

    @Override
    public void deleteOldVersionData(Long reposId, String repoName) {
        List<Bson> bsons = new ArrayList<>();
        if (reposId != null) {
            bsons.add(Filters.eq(COMMIT_REPOS_ID, reposId));
        }
        if (repoName != null) {
            bsons.add(Filters.eq(COMMIT_REPO_NAME, repoName));
        }
        bsons.add(Filters.ne(GIT_COMMIT_DATA_STRUCT_VERSION, GitCommit._data_struct_version));
        collection.deleteMany(Filters.and(bsons.toArray(new Bson[bsons.size()])));
    }
}
