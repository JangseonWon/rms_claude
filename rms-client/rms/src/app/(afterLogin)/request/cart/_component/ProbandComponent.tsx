'use client';

import style from "./cartExtensionComponent.module.css";
import InputBox from "@/app/_component/InputBox";
import React, {useEffect} from "react";
import {
    useProband,
    useRelationship,
    useSetProband,
    useSetProbandModalOpen,
    useSetRelationship
} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import SelectBox from "@/app/_component/SelectBox";
import {Extension} from "@/model/Extension";

type Props = {
    extensions?: Extension[];
}

export const ProbandComponent = ({extensions = []}: Props) => {
    const probandValue = useProband();
    const relationship = useRelationship();
    const setProbandValue = useSetProband();
    const setRelationship = useSetRelationship();
    const setProbandModal = useSetProbandModalOpen();

    const relationshipList = [
        {name: "FATHER", value: 'father'},
        {name: "MOTHER", value: 'mother'}
    ];

    const handleExtensionSetup = () => {
        extensions.forEach(extension => {
            if (extension.id?.includes('01')) {
                setProbandValue(extension.value!);
            } else if (extension.id?.includes('02')) {
                setRelationship(extension.value!);
            }
        });
    };

    const Click = () => {
        setProbandModal(true);
    };

    useEffect(() => {
        handleExtensionSetup();
    }, []);

    return (
        <div>
            <p className={style.title}>Proband Info.</p>
            <div key={'proband'} className={style.proband}>
                <SelectBox
                    key={'relationship'}
                    label={'RelationShip*'}
                    value={relationship}
                    options={relationshipList}
                    required={true}
                    onChange={(selectedOption) => setRelationship(selectedOption.name)}
                    width="200px"
                />
                <div className={style.probandInput}>
                    <InputBox
                        key={'probandInput'}
                        label={'Registration ID'}
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