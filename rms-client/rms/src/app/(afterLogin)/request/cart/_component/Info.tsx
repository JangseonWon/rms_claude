"use client"

import style from "@/app/(afterLogin)/request/cart/_component/info.module.css"
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useRouter, useSearchParams} from "next/navigation";
import SelectBox from "@/app/_component/SelectBox"
import {useCallback, useEffect, useState} from "react";
import {getRequest} from "@/app/(afterLogin)/request/cart/_api/getRequest";
import {Request} from "@/model/Request"
import InputBox from "@/app/_component/InputBox";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {getOrganization} from "@/app/(afterLogin)/request/cart/_api/getOrganization";
import {Organization} from "@/model/Organization";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import GreenButton from "@/app/_component/GreenButton";
import {updateRequest} from "@/app/(afterLogin)/request/cart/_api/updateRequest";
import {getSampleType} from "@/app/(afterLogin)/request/cart/_api/getSampleType";
import {SampleType} from "@/model/SampleType";
import DatePickerBox from "@/app/_component/DatePickerBox";


export default function Info() {
    const [request, setRequest] = useState<Request>()
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<SelectBoxOption[]>([])
    const router = useRouter();
    const searchParams = useSearchParams()
    const orderId = searchParams!.get("order")
    const serviceId = searchParams!.get("service")
    const sampleId = searchParams!.get("sample")
    const userId = searchParams!.get("user_id")

    const onClickClose = () => {
        router.back();
    };
    const handleRequestChange = (path: string, value: any) => {
        setRequest(prevState => ({
            ...prevState,
            ...setNestedValue({ ...prevState }, path, value)
        }));
    };
    const setNestedValue = (object: any, nestedPath: string, newValue: any): any => {
        const [firstKey, ...remainingPathSegments] = nestedPath.split('.');
        if (remainingPathSegments.length === 0) {
            return { ...object, [firstKey]: newValue };
        }
        if (!object[firstKey]) {
            object[firstKey] = {};
        }
        return {
            ...object,
            [firstKey]: setNestedValue(object[firstKey] || {}, remainingPathSegments.join('.'), newValue),
        };
    };
    const fetchRequest = useCallback(async () => {
        const response = await getRequest(orderId!, serviceId!, sampleId!)
        const json = await response.json()
        setRequest(json as Request)
    },[orderId, serviceId, sampleId]);

    const fetchOrganizations = useCallback(async () => {
        const response = await getOrganization(userId!);
        const data = await response.json();
        setOrganizationOptions(transformOrganizationsToOptions(data as Organization[]));
    },[userId]);

    const fetchSampleType = useCallback(async () => {
        const response = await getSampleType(serviceId!)
        const json = await response.json()
        setSampleTypeOptions(transformSampleTypeToOptions(json as SampleType[]))
    },[serviceId])

    const transformOrganizationsToOptions = (organizations: Organization[]): SelectBoxOption[] => {
        return organizations.map(org => ({
            value: org.id,
            name: org.name
        }));
    };
    const transformSampleTypeToOptions = (sampleTypes: SampleType[]): SelectBoxOption[] => {
        return sampleTypes.map(sampleType => ({
            value: sampleType.id,
            name: sampleType.name
        }));
    };
    const handleEditClick = async () => {
        if (validateRequest()) {
            const response = await updateRequest(request!);
            if (response.ok) alert("Success update")
            else alert("Fail update")
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
    const getDateFromComponents = (year?: number, month?: number, day?: number): Date | undefined => {
        if (!year || !month || !day) return undefined;
        return new Date(year, month - 1, day);  // Month is zero-based in JavaScript Date
    }

    useEffect(() => {
        fetchRequest()
        fetchOrganizations()
        fetchSampleType()
        const handleKeyPress = (event: KeyboardEvent) => {
            if (event.key === 'Escape') {router.back();}
        };
        window.addEventListener('keydown', handleKeyPress);
        return () => {window.removeEventListener('keydown', handleKeyPress);};
    }, [fetchRequest, fetchOrganizations, fetchSampleType, router]);

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
                                options={organizationOptions}
                                onChange={(value) => {
                                    handleRequestChange('sample.patient.organization.id', value.value)
                                    handleRequestChange('sample.patient.organization.name', value.name)
                                }}
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
                                onChange={(value) => handleRequestChange('sample.patient.name', value)}
                                required={true}
                            />
                            <InputBox
                                label={"MRN*"}
                                value={request.sample?.patient?.serial}
                                onChange={(value) => handleRequestChange('sample.patient.serial', value)}
                                required={true}
                            />
                            <DatePickerBox
                                label={"Date of Birth"}
                                value={getDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}
                                onChange={(date) => {
                                    handleRequestChange('sample.patient.birth_year', date.getFullYear());
                                    handleRequestChange('sample.patient.birth_month', date.getMonth());
                                    handleRequestChange('sample.patient.birth_day', date.getDay());
                                }}
                            />
                            <InputBox
                                label={"Age"}
                                value={request.sample?.age}
                                onChange={(value) => handleRequestChange('sample.age', value)}
                            />
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Specimen/.Sample Info.</p>
                            <SelectBox
                                label={"Type*"}
                                value={request.sample?.sample_type?.name}
                                options={sampleTypeOptions}
                                onChange={(value) => {
                                    handleRequestChange('sample.sample_type.id', value.value)
                                    handleRequestChange('sample.sample_type.name', value.name)
                                }}
                            />
                            <DatePickerBox
                                label={"Date or collection*"}
                                value={request.sample?.sampling_on}
                                onChange={(date) => handleRequestChange('sample.sampling_on', date)}
                            />
                            <InputBox
                                label={"Quantity*"}
                                value={request.sample?.quantity?.toString()}
                                required={true}
                                onChange={(value) => handleRequestChange('sample.quantity', value)}
                            />
                            <InputBox
                                label={"Memo"}
                                value={request.memo}
                                onChange={(value) => handleRequestChange('memo', value)}
                            />
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Additional Info.</p>
                            <InputBox
                                label={"Medical Department"}
                                value={request.department}
                                onChange={(value) => handleRequestChange('department', value)}
                            />
                            <InputBox
                                label={"Ward"}
                                value={request.ward}
                                onChange={(value) => handleRequestChange('ward', value)}
                            />
                            <InputBox
                                label={"Physician Name"}
                                value={request.physician}
                                onChange={(value) => handleRequestChange('physician', value)}
                            />
                        </div>
                        <div className={style.modalBottom}>
                            <GreenButton name={"Edit"} onClick={handleEditClick}/>
                        </div>
                    </div>
                ) : <Loading/>}
            </div>
        </div>
    )
}