/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.converter;

import org.osiam.resources.scim.Entitlement;
import org.osiam.storage.entities.EntitlementEntity;
import org.springframework.stereotype.Service;

@Service
public class EntitlementConverter implements Converter<Entitlement, EntitlementEntity> {

    @Override
    public EntitlementEntity fromScim(Entitlement scim) {
        EntitlementEntity entitlementsEntity = new EntitlementEntity();
        entitlementsEntity.setValue(String.valueOf(scim.getValue()));
        entitlementsEntity.setType(scim.getType());

        return entitlementsEntity;
    }

    @Override
    public Entitlement toScim(EntitlementEntity entity) {
        return new Entitlement.Builder()
                .setValue(entity.getValue())
                .setType(entity.getType())
                .build();
    }

}