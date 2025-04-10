'use client';

import React, {useState} from "react";
import style from "@/app/(afterLogin)/user/_component/institutionAddModal.module.css";
import globalStyle from '@/css/modal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import {useSession} from "next-auth/react";
import {putOrganization} from "@/app/(afterLogin)/user/_api/putOrganization";
import {Organization} from "@/model/Organization";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";


type Props = {
    open: boolean;
    closeModal: () => void;
}

export default function InstitutionAddModal({open, closeModal}: Props) {
    const [institutionId, setInstitutionId] = useState('');
    const [institutionName, setInstitutionName] = useState('');
    const [nursingNumber, setNursingNumber] = useState('');
    const [registrationNumber, setRegistrationNumber] = useState('');
    const [type, setType] = useState('');
    const showAlert = CallAlertDialog();
    const { data: session } = useSession();

    const handleInstitutionAdd = async () => {
        const isValidId = /^[A-Za-z0-9]{1,8}$/.test(institutionId);
        if (!isValidId) {
            showAlert("Institution ID must be exactly 8 characters long, containing only letters and numbers.");
            return;
        }

        const organization: Organization = {
            id: institutionId,
            user_id: session?.user?.id,
            name: institutionName,
            nursing_number: nursingNumber,
            registration_number: registrationNumber,
            type: type,
        }
        const response = await putOrganization(session?.user.id!, organization);
        if (response.ok) {
            showAlert("Institution added successfully!");
            closeModal();
        } else {
            showAlert("Failed to add institution");
        }
    }

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={globalStyle.modalCloseButton}/>
                <section className={style.modalBody}>
                    <section className={style.bottomBody}>
                            <div className={style.bodyGrid}>
                                <div className={style.bodyHeader}>
                                    <h2>New Institution Info</h2>
                                    <button className={style.addButton} onClick={handleInstitutionAdd}>
                                        Add
                                    </button>
                                </div>
                                <InputBox
                                    placeHolder={"8 character limit of alphabet + number"}
                                    required={true}
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