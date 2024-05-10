'use client';

import * as React from 'react';
import clsx from 'clsx';
import { styled, css } from '@mui/system';
import { Modal as BaseModal } from '@mui/base/Modal';
import style from "@/app/(afterLogin)/request/services/precision-oncology/_component/cartModal.module.css";
import InputExtension from "@/app/(afterLogin)/request/services/precision-oncology/_component/InputExtension";

export default function CartModal() {
    const [open, setOpen] = React.useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    return (
        <div>
            <TriggerButton type="button" onClick={handleOpen}>
                Add to Cart
            </TriggerButton>
            <Modal
                aria-labelledby="unstyled-modal-title"
                aria-describedby="unstyled-modal-description"
                open={open}
                onClose={handleClose}
                slots={{ backdrop: StyledBackdrop }}
            >
                <ModalContent>
                    <section className={style.firstSection}>
                        <div>
                            <div className={style.mainName}>
                                Institution name *
                            </div>
                            기관 명
                        </div>
                    </section>
                    <section className={style.middleSection}>
                        <div>
                            <div className={style.mainName}>
                                Service Info.
                            </div>
                            <div className={style.subName}>
                                Service *
                            </div>
                            서비스 명
                        </div>
                    </section>
                    <section className={style.middleSection}>
                        <div className={style.mainName}>
                            Patient Info.
                        </div>
                        <section className={style.subSection}>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Name *
                                </div>
                                이름
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    MRN *
                                </div>
                                MRN 명
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Date of Birth
                                </div>
                                날짜
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Age
                                </div>
                                나이
                            </div>
                        </section>
                    </section>
                    <section className={style.middleSection}>
                        <div className={style.mainName}>
                            Specimen/ .Sample Info.
                        </div>
                        <section className={style.subSection}>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Type *
                                </div>
                                타입
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Date of collection *
                                </div>
                                날짜
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Quantity *
                                </div>
                                수량
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Memo
                                </div>
                                메모
                            </div>
                        </section>
                    </section>
                    <section className={style.middleSection}>
                        <div className={style.mainName}>
                            Additional Info.
                        </div>
                        <section className={style.subSection}>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Medical Department
                                </div>
                                의료기관
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Ward
                                </div>
                                동
                            </div>
                            <div className={style.subItem}>
                                <div className={style.subName}>
                                    Physician Name
                                </div>
                                이름
                            </div>
                        </section>
                    </section>
                    <section className={style.bottomSection}>
                        <InputExtension/>
                    </section>
                    <button className={style.cart}>
                        Cart
                    </button>
                </ModalContent>
            </Modal>
        </div>
    );
}

const Backdrop = React.forwardRef<
    HTMLDivElement,
    { open?: boolean; className: string }
>((props, ref) => {
    const {open, className, ...other} = props;
    return (
        <div
            className={clsx({'base-Backdrop-open': open }, className)}
            ref={ref}
            {...other}
        />
    );
});

const Modal = styled(BaseModal)`
  position: fixed;
  z-index: 1300;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
    border-radius: 200px;
`;

const StyledBackdrop = styled(Backdrop)`
  z-index: -1;
  position: fixed;
  inset: 0;
  background-color: rgb(0 0 0 / 0.5);
  -webkit-tap-highlight-color: transparent;
`;

const ModalContent = styled('div')(
    ({ theme }) => css`
    font-family: 'IBM Plex Sans', sans-serif;
    font-weight: 500;
    text-align: start;
    position: relative;
    display: flex;
    flex-direction: column;
    gap: 8px;
    overflow: hidden;
    background-color: #fff;
    border-radius: 15px;
    border: 1px solid #DAE2ED;
    box-shadow: 0 4px 12px rgb(0 0 0 / 0.2);
    padding: 24px;
    color: #1C2025;
  `,
);

const TriggerButton = styled('button')(
    ({ theme }) => css`
    font-weight: 700;
    font-size: 0.875rem;
    padding: 6px 10px;
    width: 110px;
    border-radius: 15px;
    border: 1px solid #90BA2D;
    transition: all 150ms ease;
    cursor: pointer;
    background-color: #ffffff;
    color: #002b49;
    margin: 10px;
    box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);

    &:hover {
        background: linear-gradient(135deg, #a1b86a, #dae8ba);
        color: #ffffff;
        border: solid #dae8ba thin;
    }
  `,
);