package com.benderski.hata.telegram.dialog;

import com.benderski.hata.subscription.PropertyType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DialogProvider implements InitializingBean {

    @Autowired
    @Qualifier("createFlatFilterDialog")
    private DialogFlow flatDialog;

    @Autowired
    @Qualifier("createRoomFilterDialog")
    private DialogFlow roomDialog;

    @Autowired
    private DialogStateStorage dialogStateStorage;

    private Map<String, DialogFlow> dialogsMap;

    @Override
    public void afterPropertiesSet() {
        dialogsMap = Stream.of(flatDialog, roomDialog)
                .collect(Collectors.toMap(DialogFlow::getId, Function.identity()));
    }

    /**
     *
     * @param userId
     * @return
     */
    public DialogFlow getDialog(Integer userId) {
        return dialogsMap.get(dialogStateStorage.getActiveDialog(userId));
    }

    public void setFlatDialog(DialogFlow flatDialog) {
        this.flatDialog = flatDialog;
    }

    public void setRoomDialog(DialogFlow roomDialog) {
        this.roomDialog = roomDialog;
    }

    public void setDialogStateStorage(DialogStateStorage dialogStateStorage) {
        this.dialogStateStorage = dialogStateStorage;
    }
}
