import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatasourceComponent } from './datasource.component';

describe('DatasourceComponent', () => {
  let component: DatasourceComponent;
  let fixture: ComponentFixture<DatasourceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatasourceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DatasourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
