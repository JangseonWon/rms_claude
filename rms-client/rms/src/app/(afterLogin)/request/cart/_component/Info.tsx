"use client"

import style from "@/app/(afterLogin)/request/cart/_component/info.module.css"
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useRouter, useSearchParams} from "next/navigation";
import SelectBox from "@/app/_component/SelectBox"
import {useEffect, useState} from "react";
import {getRequest} from "@/app/(afterLogin)/request/cart/_api/getRequest";
import {Request} from "@/model/Request"
import InputBox from "@/app/_component/InputBox";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {getOrganization} from "@/app/(afterLogin)/request/cart/_api/getOrganization";
import {Organization} from "@/model/Organization";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import GreenButton from "@/app/_component/GreenButton";
import {updateRequest} from "@/app/(afterLogin)/request/cart/_api/updateRequest";


export default function Info() {
    const [request, setRequest] = useState<Request>()
    const [options, setOptions] = useState<SelectBoxOption[]>([])
    const router = useRouter();
    const searchParams = useSearchParams()
    const orderId = searchParams.get("order")
    const serviceId = searchParams.get("service")
    const sampleId = searchParams.get("sample")
    const userId = searchParams.get("user_id")

    const onClickClose = () => {
        router.back();
    };
    const handleChange = (path: string, value: string) => {
        setRequest(prevState => ({
            ...prevState,
            ...setNestedValue({ ...prevState }, path, value)
        }));
    };
    const setNestedValue = (obj: any, path: string, value: any) => {
        const keys = path.split('.');
        let temp = obj;
        keys.slice(0, -1).forEach(key => {
            if (!temp[key]) temp[key] = {};
            temp = temp[key];
        });
        temp[keys[keys.length - 1]] = value;
        return temp;
    };

    useEffect(() => {
        fetchRequest()
        fetchOrganization()
        const handleKeyPress = (event: KeyboardEvent) => {
            if (event.key === 'Escape') {router.back();}
        };
        window.addEventListener('keydown', handleKeyPress);
        return () => {window.removeEventListener('keydown', handleKeyPress);};
    }, [router]);

    const fetchRequest = async () => {
        const response = await getRequest(orderId!, serviceId!, sampleId!);
        const data = await response.json();
        setRequest(data as Request)
    };
    const fetchOrganization = async () => {
        const response = await getOrganization(userId!);
        const data = await response.json();
        setOptions(transformOrganizationsToOptions(data as Organization[]));
    };
    const transformOrganizationsToOptions = (organizations: Organization[]): SelectBoxOption[] => {
        return organizations.map(org => ({
            value: org.id,
            name: org.name
        }));
    };
    const handleEditClick = () => {
        if (validateRequest()) {
            updateRequest(request!)
        } else {
            alert("Please fill out all required fields.");
        }
    };
    const validateRequest = () => {
        if (!request?.sample?.patient?.name) return false;
        if (!request?.sample?.patient?.serial) return false;
        if (!request?.sample?.sample_type?.name) return false;
        if (!request?.sample?.quantity) return false;
        return true;
    };

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <div className={style.modalTitle}>
                    <h1>Order Details</h1>
                    <button onClick={onClickClose}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </button>
                </div>
                {request ? (
                    <div className={style.modalContent}>
                        <div className={style.content}>
                            <p className={style.title}>Institution name*</p>
                            <SelectBox
                                label={""}
                                value={request.sample?.patient?.organization?.name}
                                options={options}
                                onChange={(value) => handleChange('sample.patient.organization.id', value)}
                            />
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Service Info.</p>
                            <InputBox
                                label={"Service"}
                                value={request.service?.name}
                                disabled={true}
                            />
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Patient Info.</p>
                            <InputBox
                                label={"Name*"}
                                value={request.sample?.patient?.name}
                                onChange={(value) => handleChange('sample.patient.name', value)}
                                required={true}
                            />
                            <InputBox
                                label={"MRN*"}
                                value={request.sample?.patient?.serial}
                                onChange={(value) => handleChange('sample.patient.serial', value)}
                                required={true}
                            />
                            <InputBox label={"Date of Birth"} value={request.sample?.patient?.birth_year?.toString()} disabled={false}/>
                            <InputBox
                                label={"Age"}
                                value={request.sample?.age?.toString()}
                                disabled={false}
                                onChange={(value) => handleChange('sample.age', value)}
                            />
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Specimen/.Sample Info.</p>
                            <InputBox
                                label={"Type*"}
                                value={request.sample?.sample_type?.name}
                                disabled={false}
                                onChange={(value) => handleChange('sample.sample_type.name', value)}
                            />
                            <InputBox label={"Date or collection*"} value={request.sample?.sampling_on} disabled={false}/>
                            <InputBox
                                label={"Quantity*"}
                                value={request.sample?.quantity?.toString()}
                                disabled={false}
                                required={true}
                                onChange={(value) => handleChange('sample.quantity', value)}
                            />
                            <InputBox
                                label={"Memo"}
                                value={request.memo}
                                disabled={false}
                                onChange={(value) => handleChange('memo', value)}
                            />
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Additional Info.</p>
                            <InputBox label={"Medical Department"} value={request.department} disabled={false}/>
                            <InputBox label={"Ward"} value={request.ward} disabled={false}/>
                            <InputBox label={"Physician Name"} value={request.physician} disabled={false}/>
                        </div>
                    </div>
                ) : <Loading/>}
                <div className={style.modalBottom}>
                    <GreenButton name={"Edit"} onClick={handleEditClick}/>
                </div>
            </div>
        </div>
    )
}