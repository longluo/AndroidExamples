/*
 * Copyright  2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#ifndef ANDROID_FXLAB_PASSTHROUGHDESCRIPTION_H
#define ANDROID_FXLAB_PASSTHROUGHDESCRIPTION_H


#include "EffectDescription.h"

namespace Effect {

class PassthroughDescription : public EffectDescription<PassthroughDescription, 0> {
public:

    static constexpr std::string_view getName() {
        return std::string_view("Passthrough");
    }

    static constexpr std::string_view getCategory() {
        return std::string_view("None");
    }

    static constexpr std::array<ParamType, getNumParams()> getParams() {
        return std::array<ParamType, 0>();
    }

    template<class iter_type>
    static _ef<iter_type> buildEffect(std::array<float, getNumParams()> ) {
        return [](iter_type, iter_type) {};
    }

};

} // namespace Effect

#endif //ANDROID_FXLAB_PASSTHROUGHDESCRIPTION_H
