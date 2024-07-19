'use client';

import React, {useState} from "react";
import style from "@/app/(afterLogin)/user/_component/institutionEditModal.module.css";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {useSession} from "next-auth/react";
import {Organization} from "@/model/Organization";
import {PatchOrganization} from "@/app/(afterLogin)/user/_api/patchOrganization";


type Props = {
    organization: Organization,
    open: boolean;
    closeModal: () => void;
}

export default function InstitutionEditModal({organization, open, closeModal}: Props) {
    const [institutionId, setInstitutionId] = useState(organization.id);
    const [institutionName, setInstitutionName] = useState(organization.name);
    const [nursingNumber, setNursingNumber] = useState(organization.nursing_number);
    const [registrationNumber, setRegistrationNumber] = useState(organization.registration_number);
    const [type, setType] = useState(organization.type);
    const { data: session } = useSession();

    const handleInstitutionEdit = async () => {
        const organization: Organization = {
            id: institutionId,
            user_id: session?.user?.id,
            name: institutionName,
            nursing_number: nursingNumber,
            registration_number: registrationNumber,
            type: type,
        }
        const response = await PatchOrganization(organization);
        if (response.ok) {
            alert("Institution update successfully!");
            closeModal();
        } else {
            alert("Failed to update institution");
        }
    }

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <section className={style.modalHeader}>
                    <div className={style.modalClose} onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <section className={style.bottomBody}>
                            <div className={style.bodyGrid}>
                                <div className={style.bodyHeader}>
                                    <h2>Institution Update</h2>
                                    <button className={style.addButton} onClick={handleInstitutionEdit}>
                                        Update
                                    </button>
                                </div>
                                <InputBox
                                    disabled={true}
                                    label={"Institution Id"}
                                    value={institutionId}
                                    onChange={setInstitutionId}
                                    type="institutionId"
                                />
                                <InputBox
                                    label={"Institution Name"}
                                    value={institutionName}
                                    onChange={setInstitutionName}
                                    type="institutionName"
                                />
                                <InputBox
                                    label={"Nursing Number"}
                                    value={nursingNumber}
                                    onChange={setNursingNumber}
                                    type="nursingNumber"
                                />
                                <InputBox
                                    label={"Registration Number"}
                                    value={registrationNumber}
                                    onChange={setRegistrationNumber}
                                    type="registrationNumber"
                                />
                                <InputBox
                                    label={"Type"}
                                    value={type}
                                    onChange={setType}
                                    type="type"
                                />
                        </div>
                    </section>
                </section>
            </div>
        </div>
    )
}