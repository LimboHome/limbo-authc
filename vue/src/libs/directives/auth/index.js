/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 指令用法 v-auth="'{role.projectEdit}'"
 */
export default {

    bind(el, binding, vn) {
        // 获取计算器
        debugger
        let $store = vn.context.$store;
        const evaluator = $store.state.session.authExpEvaluator;
        const expression = binding.value;

        if (!evaluator.evaluate(expression)) {
            el.style.display = 'none';
        }
    },

}