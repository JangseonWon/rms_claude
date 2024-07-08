import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/user/_component/institutionModal.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {Organization} from "@/model/Organization";
import {getInstitutions} from "@/app/(afterLogin)/request/management/user/_api/getInstitutions";

type Props = {
    id: string;
    name: string;
    open: boolean;
    closeModal: () => void;
}

export default function InstitutionModal({id, name, open, closeModal}: Props) {
    const [institutionData, setInstitutionData] = useState<Organization[]>([]);


    const fetchInstitutionData = async (userId: string) => {
        const response = await getInstitutions(userId);
        const data = await response.json();
        setInstitutionData(data as Organization[]);
    }

    useEffect(() => {
        if (open) {
            fetchInstitutionData(id);
        }
    }, [id, open]);

    if (!open) return null;


    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <section className={style.modalHeader}>
                    <div className={style.modalClose} onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </div>
                    <div className={style.modalTop}>
                        <div className={style.title}>Institution Details</div>
                        <div className={style.institutionTitle}>
                            User Id : {id} / Name : {name}
                        </div>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <div className={style.leftBody}>
                        <table className={style.institutionTable}>
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Name</th>
                                <th>Code</th>
                                <th>Nursing Number</th>
                                <th>Type</th>
                            </tr>
                            </thead>
                            <tbody>
                            {institutionData && institutionData.length > 0 && institutionData.map((row, rowIndex) => (
                                <tr key={rowIndex}>
                                    <td>{row.id}</td>
                                    <td>{row.name}</td>
                                    <td>{row.registration_number}</td>
                                    <td>{row.nursing_number}</td>
                                    <td>{row.type}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
    )
}