import React, {useEffect, useState} from "react";
// import style from "@/app/(afterLogin)/request/management/user/_component/institutionModal.module.css";
import style from '@/css/modal.module.css';
import tableStyle from '@/css/globalTable.module.css';
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
        if (response.ok) {
            const data = await response.json();
            setInstitutionData(data as Organization[]);
        } else {
            setInstitutionData([]);
        }
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
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={style.modalCloseButton}/>
                <div className={style.modalTitle}>Institution Details</div>
                <div className={style.modalSubTitle}>
                    User Id : {id} / Name : {name}
                </div>
                <section className={style.modalBody}>
                    <div className={style.leftBody}>
                        <table className={tableStyle.table}>
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