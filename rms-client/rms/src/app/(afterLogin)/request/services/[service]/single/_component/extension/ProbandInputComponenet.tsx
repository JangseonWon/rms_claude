'use client';

import style
    from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/extensionInputComponent.module.css";
import InputBox from "@/app/_component/InputBox";
import React, {useEffect, useState} from "react";
import {
    useProband,
    useRelationship,
    useSetProband,
    useSetProbandModalOpen,
    useSetRelationship
} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import SelectBox from "@/app/_component/SelectBox";
import {getRequestRelations} from "@/app/(afterLogin)/request/services/[service]/single/_api/getRequestRelations";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {RequestRelation} from "@/model/RequestRelation";

export const ProbandInputComponent = () => {
    const probandValue = useProband();
    const setProbandValue = useSetProband();
    const relationship = useRelationship();
    const setRelationship = useSetRelationship();
    const setProbandModal = useSetProbandModalOpen();
    const [options, setOptions] = useState<SelectBoxOption[]>()

    const Click = () => {
        setProbandModal(true);
    }

    const fetchRequestGroup = async () => {
        const response = await getRequestRelations()
        if (response.ok) {
            const data = await response.json();
            setOptions(transformRequestRelationToOptions(data as RequestRelation[]));
        }

    }
    const transformRequestRelationToOptions = (data: RequestRelation[]): SelectBoxOption[] => {
        return data
            .filter(value => value.id !==1)
            .map(value => ({
                value: value.id,
                name: value.name
            }));
    };

    useEffect(() => {
        fetchRequestGroup()
    }, []);


    return (
        <div>
            <p className={style.title}>Proband Info.</p>
            <div key={'proband'} className={style.proband}>
                <SelectBox
                    key={'relationship'}
                    label={'RelationShip*'}
                    value={relationship}
                    options={options}
                    required={true}
                    onChange={(selectedOption) => setRelationship(selectedOption.name)}
                    width="200px"
                />
                <div className={style.probandInput}>
                    <InputBox
                        key={'probandInput'}
                        label={'Proband Number'}
                        required={true}
                        disabled={true}
                        value={probandValue}
                        onChange={(inputValue) => setProbandValue(inputValue)}
                    />
                </div>
                <button className={style.button} onClick={Click}>Click here to find proband</button>
            </div>
        </div>
    )
}