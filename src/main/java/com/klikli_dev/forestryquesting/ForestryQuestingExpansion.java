package com.tao.forestryquesting;

import betterquesting.api.api.IQuestExpansion;
import betterquesting.api.api.QuestExpansion;

/**
 * Created by Tao on 12/14/2017.
 */
@QuestExpansion
public class ForestryQuestingExpansion implements IQuestExpansion {
    @Override
    public void loadExpansion() {
        ForestryQuestingMod.proxy.registerExpansion();
    }
}
