package top.jach.tes.plugin.tes.code.git.commit;

import org.apache.commons.lang3.builder.Diff;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2020/6/21
 */
public interface DiffFileRepository {
    ObjectId saveDiffFile(DiffFile diffFile, Long reposId, String repoName, String sha);
    void saveDiffFiles(List<DiffFile> diffFiles, Long reposId, String repoName, String sha);
    List<DiffFile> findDiffFilesBySHA(String repoId, String repoName, String sha);
    DiffFile findDiffFileById(ObjectId id);
}
