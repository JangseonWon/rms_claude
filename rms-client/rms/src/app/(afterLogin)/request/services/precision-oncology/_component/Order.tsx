'use client';

import style from "@/app/(afterLogin)/request/services/precision-oncology/_component/order.module.css"
import InputExtension from "@/app/(afterLogin)/request/services/precision-oncology/_component/InputExtension";
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import {useEffect, useState} from "react";
import {Organization} from "@/model/Organization";
import {getOrganization} from "@/app/(afterLogin)/request/services/precision-oncology/_api/getOrganization";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {getServices} from "@/app/(afterLogin)/request/services/precision-oncology/_api/getServices";
import {Service} from "@/model/Service";
import {Request} from "@/model/Request";
import DatePickerBox from "@/app/_component/DatePickerBox";
import {SampleType} from "@/model/SampleType";
import {getSampleType} from "@/app/(afterLogin)/request/services/precision-oncology/_api/getSampleType"
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {updateRequest} from "@/app/(afterLogin)/request/cart/_api/updateRequest";
import {putRequest} from "@/app/(afterLogin)/request/services/precision-oncology/_api/putRequest";
import {useRouter} from "next/navigation";

export default function Order() {
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [serviceOptions, setServiceOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<SelectBoxOption[]>([])
    const [request, setRequest] = useState<Request>({})
    const router = useRouter();

    useEffect(() => {
        fetchOrganizations()
        fetchServices()
    }, []);
    const fetchOrganizations = async () => {
        const response = await getOrganization()
        const data = await response.json();
        setOrganizationOptions(transformOrganizationToOptions(data as Organization[]))
    };
    const fetchServices = async () => {
        const response = await getServices()
        const data = await response.json();
        setServiceOptions(transformServiceToOptions(data as Service[]))
    };
    const fetchSampleType = async (serviceId: string) => {
        const response = await getSampleType(serviceId)
        const data = await response.json();
        setSampleTypeOptions(transformSampleTypeToOptions(data as SampleType[]))
    }

    const publishRequest = (status:string) => {
        const updateRequest = {
            ...request, ...{status: status}
        }
        putRequest(updateRequest)
            .then((res) =>{
                if(res.ok) {
                    alert("success!")
                }
                else alert("fail")
            })
    }
    const handleServiceChange = (value: SelectBoxOption):void => {
        fetchSampleType(value.value!)
    }
    const handleRequestChange = (path: string, value: any):void => {
        setRequest(prevState => ({
            ...prevState,
            ...setNestedValue({...prevState}, path, value)
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

    const transformOrganizationToOptions = (data: Organization[]): SelectBoxOption[] => {
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };
    const transformServiceToOptions = (data: Service[]): SelectBoxOption[] => {
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };
    const transformSampleTypeToOptions = (data: SampleType[]): SelectBoxOption[] => {
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };
    const isAllRequiredFilled = () => {
        if (!request?.sample?.patient?.name) return false;
        if (!request?.sample?.patient?.serial) return false;
        if (!request?.sample?.sample_type?.id) return false;
        if (!request?.sample?.sampling_on) return false;
        if (!request?.sample?.quantity) return false;
        return true;
    };

    return (
        <div className={style.container}>
            <div className={style.buttonSection}>
                <GreenButton
                    name={"Add to Cart"}
                    disabled={!isAllRequiredFilled()}
                    onClick={() => publishRequest("CART")}
                />
                <BlueButton
                    name={"Order Now"}
                    disabled={!isAllRequiredFilled()}
                    onClick={() => publishRequest("ORDERD")}
                />
            </div>
            <p className={style.mainName}>Institution name *</p>
            <div className={style.section}>
                <SelectBox
                    label={""}
                    options={organizationOptions}
                    required={true}
                    onChange={(value) => {
                        handleRequestChange('sample.patient.organization.id', value.value)
                        handleRequestChange('sample.patient.organization.name', value.name)
                    }}
                />
            </div>
            <p className={style.mainName}>Service Info.</p>
            <div className={style.section}>
                <SelectBox
                    label={"Service*"}
                    options={serviceOptions}
                    required={true}
                    onChange={(value) => {
                        handleServiceChange(value)
                        handleRequestChange('service.id', value.value)
                        handleRequestChange('service.name', value.name)
                    }}
                />
            </div>
            <p className={style.mainName}>Patient Info.</p>
            <div className={style.section}>
                <InputBox
                    label={"Name*"}
                    required={true}
                    onChange={(value) => handleRequestChange('sample.patient.name', value)}
                />
                <InputBox
                    label={"MRN*"}
                    required={true}
                    onChange={(value) => handleRequestChange('sample.patient.serial', value)}
                />
                <DatePickerBox
                    label={"Date of Birth"}
                    onChange={(date) => {
                        handleRequestChange('sample.patient.birth_year', date.getFullYear());
                        handleRequestChange('sample.patient.birth_month', date.getMonth());
                        handleRequestChange('sample.patient.birth_day', date.getDay());
                    }}
                />
                <InputBox
                    label={"Age"}
                    onChange={(value) => handleRequestChange('sample.age', value)}
                />
            </div>
            <p className={style.mainName}>Specimen/ .Sample Info.</p>
            <div className={style.section}>
                <SelectBox
                    label={"Type*"}
                    options={sampleTypeOptions}
                    required={true}
                    onChange={(value) => {
                        handleRequestChange('sample.sample_type.id', value.value)
                        handleRequestChange('sample.sample_type.name', value.name)
                    }}
                />
                <DatePickerBox
                    label={"Date of Collection*"}
                    required={true}
                    onChange={(date) => handleRequestChange('sample.sampling_on', date)}
                />
                <InputBox
                    label={"Quantity*"}
                    required={true}
                    onChange={(value) => handleRequestChange('sample.quantity', value)}
                />
                <InputBox
                    label={"Memo"}
                    onChange={(value) => handleRequestChange('memo', value)}
                />
            </div>
            <p className={style.mainName}>Additional Info.</p>
            <div className={style.section}>
                <InputBox
                    label={"Medical Department"}
                    onChange={(value) => handleRequestChange('department', value)}
                />
                <InputBox
                    label={"Ward"}
                    onChange={(value) => handleRequestChange('ward', value)}
                />
                <InputBox
                    label={"Physician Name"}
                    onChange={(value) => handleRequestChange('physician', value)}
                />
            </div>
            <section className={style.bottomSection}>
                <InputExtension/>
            </section>
        </div>
    )
}